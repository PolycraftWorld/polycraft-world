package edu.utd.minecraft.mod.polycraft.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.microsoft.azure.storage.AccessCondition;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.microsoft.azure.storage.blob.ListBlobItem;

import edu.utd.minecraft.mod.polycraft.privateproperty.PrivateProperty;

public class WorldMerge {

    private static final int storageLeaseAcquireRetries = 2;
    private static final int storageLeaseSeconds = 30;
    private static final boolean verbose = false;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss z");

    public static void main(String[] args) throws IOException, URISyntaxException, InvalidKeyException, StorageException {
        int arg = 0;
        String privatePropertiesEndPointFormat = args[arg++];
        if (privatePropertiesEndPointFormat.startsWith("http") && !privatePropertiesEndPointFormat.endsWith("/"))
            privatePropertiesEndPointFormat = privatePropertiesEndPointFormat + "/";
        privatePropertiesEndPointFormat += "worlds/%s/%s/";
        final String update = args[arg++].toLowerCase();
        final boolean branchUpdate = update.contains("branch");
        final boolean trunkUpdate = update.contains("trunk");
        if (!(branchUpdate || trunkUpdate)) {
            System.out.println("Please specify either branch, trunk, or branch|trunk to update");
            return;
        }

        final String branchWorldDir = args[arg++];
        final String worldName = new File(branchWorldDir).getName();

        Date newLastMerged = new Date();
        Date previousLastMerged = null;
        final Properties properties = new Properties();
        final File propertiesFile = new File("worldmerge.properties");
        if (propertiesFile.exists()) {
            try {
                properties.load(new FileInputStream(propertiesFile));
                previousLastMerged = dateFormat.parse(properties.getProperty(worldName + ".lastmerged"));
            }
            catch (final Exception e) {}
        }

        final Map<String, Set<String>> branchPrivatePropertyChunksByRegion = getPrivatePropertyChunksByRegion(String.format(privatePropertiesEndPointFormat, "include", worldName), true);
        final Map<String, Set<String>> trunkPrivatePropertyChunksByRegion = getPrivatePropertyChunksByRegion(String.format(privatePropertiesEndPointFormat, "exclude", worldName), false);
        final Set<String> affectedRegions = new HashSet<String>(trunkPrivatePropertyChunksByRegion.keySet());
        affectedRegions.addAll(branchPrivatePropertyChunksByRegion.keySet());

        final String trunkType = args[arg++];
        if ("file".equalsIgnoreCase(trunkType)) {
            final String trunkWorldDir = args[arg++];
            runProgramFile(previousLastMerged, branchUpdate, trunkUpdate, affectedRegions,
                    branchPrivatePropertyChunksByRegion, trunkPrivatePropertyChunksByRegion, branchWorldDir, trunkWorldDir);
        }
        else if ("storage".equalsIgnoreCase(trunkType)) {
            final String trunkStorageAccountName = args[arg++];
            final String trunkStorageAccountKey = args[arg++];
            final String trunkStorageContainerName = args[arg];
            runProgramStorage(previousLastMerged, branchUpdate, trunkUpdate, affectedRegions,
                    branchPrivatePropertyChunksByRegion, trunkPrivatePropertyChunksByRegion,
                    branchWorldDir, trunkStorageAccountName, trunkStorageAccountKey, trunkStorageContainerName);
        }

        //save off the new chunk date so we can save ourselves processing time next go around
        properties.put(worldName + ".lastmerged", dateFormat.format(newLastMerged));
        properties.store(new FileOutputStream(propertiesFile), null);
    }

    private static void runProgramFile(
            final Date lastMerged, final boolean branchUpdate, final boolean trunkUpdate,
            final Set<String> affectedRegions, final Map<String, Set<String>> branchChunksByRegion, final Map<String, Set<String>> trunkChunksByRegion,
            final String branchWorldDir, final String trunkWorldDir) throws IOException {
        final Map<String, RegionFile> branchRegionFiles = RegionFile.find(branchWorldDir, lastMerged);
        final Map<String, RegionFile> trunkRegionFiles = RegionFile.find(trunkWorldDir, lastMerged);
        final String branchRegionDir = RegionFile.getDirFromWorld(branchWorldDir);
        final String trunkRegionDir = RegionFile.getDirFromWorld(trunkWorldDir);

        long trunkUpdatedCount = 0;
        long trunkUpdatedBytes = 0;
        long branchUpdatedCount = 0;
        long branchUpdatedBytes = 0;
        for (final String regionIdentifier : affectedRegions) {
            final Set<String> branchChunks = branchChunksByRegion.get(regionIdentifier);
            final Set<String> trunkChunks = trunkChunksByRegion.get(regionIdentifier);
            RegionFile branchRegionFile = branchRegionFiles.get(regionIdentifier);
            RegionFile trunkRegionFile = trunkRegionFiles.get(regionIdentifier);
            if (branchRegionFile != null || trunkRegionFile != null) {
                if (trunkUpdate) {
                    if (branchRegionFile != null && branchChunks != null) {
                        if (trunkRegionFile == null) {
                            trunkRegionFile = new RegionFile(trunkRegionDir, branchRegionFile.x, branchRegionFile.z);
                        }
                        trunkUpdatedBytes += trunkRegionFile.write(RegionFile.merge(trunkRegionFile.x, trunkRegionFile.z, trunkRegionFile.read(), branchRegionFile.read(), branchChunks, verbose));
                        trunkUpdatedCount++;
                    }
                    //no branch file or branch chunks, nothing to update the trunk with
                }

                if (branchUpdate) {
                    if (trunkRegionFile != null && trunkChunks != null) {
                        if (branchRegionFile == null) {
                            branchRegionFile = new RegionFile(branchRegionDir, trunkRegionFile.x, trunkRegionFile.z);
                        }
                        branchUpdatedBytes += branchRegionFile.write(RegionFile.merge(branchRegionFile.x, branchRegionFile.z, branchRegionFile.read(), trunkRegionFile.read(), trunkChunks, verbose));
                        branchUpdatedCount++;
                    }
                    //no trunk file or trunk chunks, nothing to update the branch with
                }
            }
        }

        if (trunkUpdatedCount > 0)
            System.out.println(String.format("%d trunk region files updated (%s)", trunkUpdatedCount, Format.humanReadableByteCount(trunkUpdatedBytes, false)));
        if (branchUpdatedCount > 0)
            System.out.println(String.format("%d branch region files updated (%s)", branchUpdatedCount, Format.humanReadableByteCount(branchUpdatedBytes, false)));
    }

    private static void runProgramStorage(
            final Date lastMerged, final boolean branchUpdate, final boolean trunkUpdate,
            final Set<String> affectedRegions, final Map<String, Set<String>> branchChunksByRegion, final Map<String, Set<String>> trunkChunksByRegion,
            final String branchWorldDir, final String trunkStorageAccountName, final String trunkStorageAccountKey, final String trunkStorageContainerName) throws IOException, URISyntaxException, InvalidKeyException, StorageException {
        final Map<String, RegionFile> branchRegionFiles = RegionFile.find(branchWorldDir, lastMerged);
        final String branchRegionDir = RegionFile.getDirFromWorld(branchWorldDir);

        final CloudStorageAccount trunkStorageAccount = CloudStorageAccount.parse(String.format("DefaultEndpointsProtocol=http;AccountName=%s;AccountKey=%s", trunkStorageAccountName, trunkStorageAccountKey));
        final CloudBlobClient trunkServiceClient = trunkStorageAccount.createCloudBlobClient();
        final CloudBlobContainer trunkStorageContainer = trunkServiceClient.getContainerReference(trunkStorageContainerName);
        final Map<String, CloudBlockBlob> trunkRegionFileContainerBlobs = new HashMap<String, CloudBlockBlob>();
        //FIXME figure out how to only list blobs after chunkDate
        //http://blogs.msdn.com/b/avkashchauhan/archive/2011/07/29/programatically-deleting-older-blobs-in-windows-azure-storage.aspx
        for (final ListBlobItem trunkRegionFileListBlobItem : trunkStorageContainer.listBlobs()) {
            if (trunkRegionFileListBlobItem instanceof CloudBlockBlob) {
                final CloudBlockBlob trunkRegionFileBlob = (CloudBlockBlob)trunkRegionFileListBlobItem;
                if (lastMerged == null || trunkRegionFileBlob.getProperties().getLastModified().after(lastMerged)) {
                    trunkRegionFileContainerBlobs.put(trunkRegionFileBlob.getName(), trunkRegionFileBlob);
                }
            }
        }

        long trunkUpdatedCount = 0;
        long trunkUpdatedBytes = 0;
        long branchUpdatedCount = 0;
        long branchUpdatedBytes = 0;
        for (final String regionIdentifier : affectedRegions) {
            final Set<String> branchChunks = branchChunksByRegion.get(regionIdentifier);
            final Set<String> trunkChunks = trunkChunksByRegion.get(regionIdentifier);

            RegionFile branchRegionFile = branchRegionFiles.get(regionIdentifier);
            byte[] trunkRegionFileBytes = null;
            CloudBlockBlob trunkRegionFileBlob = trunkRegionFileContainerBlobs.get(regionIdentifier);
            AccessCondition leaseAccessCondition = null;
            if (trunkRegionFileBlob != null) {
                if (trunkUpdate) {
                    leaseAccessCondition = new AccessCondition();
                    int retry = 0;
                    for (; retry < storageLeaseAcquireRetries; retry++) {
                        try {
                            leaseAccessCondition.setLeaseID(trunkRegionFileBlob.acquireLease(storageLeaseSeconds, UUID.randomUUID().toString()));
                            break;
                        }
                        catch (final StorageException e) {
                            if (retry + 1 == storageLeaseAcquireRetries) {
                                throw e;
                            }
                        }
                    }
                }
                final ByteArrayOutputStream trunkRegionFileOutputStream = new ByteArrayOutputStream();
                trunkRegionFileBlob.download(trunkRegionFileOutputStream, leaseAccessCondition, null, null);
                trunkRegionFileBytes = trunkRegionFileOutputStream.toByteArray();
            }

            if (branchRegionFile != null || trunkRegionFileBytes != null) {
                if (trunkUpdate) {
                    if (branchRegionFile != null && branchChunks != null) {
                        final byte[] mergedBytes = RegionFile.merge(branchRegionFile.x, branchRegionFile.z, trunkRegionFileBytes, branchRegionFile.read(), branchChunks, verbose);
                        if (trunkRegionFileBlob == null) {
                            trunkRegionFileBlob = trunkStorageContainer.getBlockBlobReference(regionIdentifier);
                        }
                        System.out.println(String.format("Uploading region file (%s): %s", Format.humanReadableByteCount(mergedBytes.length, false), trunkRegionFileBlob.getUri().toString()));
                        trunkRegionFileBlob.uploadFromByteArray(mergedBytes, 0, mergedBytes.length, leaseAccessCondition, null, null);
                        trunkUpdatedBytes += mergedBytes.length;
                        trunkUpdatedCount++;
                    }
                    //no branch file or chunks, so nothing to update the trunk with
                }

                if (branchUpdate) {
                    if (trunkRegionFileBytes != null && trunkChunks != null) {
                        final int[] parsedRegionIdentifier = RegionFile.parseIdentifier(regionIdentifier);
                        if (branchRegionFile == null) {
                            branchRegionFile = new RegionFile(branchRegionDir, parsedRegionIdentifier[0], parsedRegionIdentifier[1]);
                        }
                        branchUpdatedBytes += branchRegionFile.write(RegionFile.merge(branchRegionFile.x, branchRegionFile.z, branchRegionFile.read(), trunkRegionFileBytes, trunkChunks, verbose));
                        branchUpdatedCount++;
                    }
                    //no trunk file or chunks, so nothing to update the branch with
                }
            }

            if (leaseAccessCondition != null) {
                trunkRegionFileBlob.releaseLease(leaseAccessCondition);
            }
        }

        if (trunkUpdatedCount > 0)
            System.out.println(String.format("%d trunk region files updated (%s)", trunkUpdatedCount, Format.humanReadableByteCount(trunkUpdatedBytes, false)));
        if (branchUpdatedCount > 0)
            System.out.println(String.format("%d branch region files updated (%s)", branchUpdatedCount, Format.humanReadableByteCount(branchUpdatedBytes, false)));
    }

    private static Map<String, Set<String>> getPrivatePropertyChunksByRegion(final String privatePropertiesUrl, final boolean master) throws IOException {
        final GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(PrivateProperty.class, new PrivateProperty.Deserializer(master));
        final Gson gson = gsonBuilder.create();
        final Collection<PrivateProperty> privateProperties = gson.fromJson(NetUtil.getText(privatePropertiesUrl), new TypeToken<Collection<PrivateProperty>>() {
        }.getType());

        final Map<String, Set<String>> chunksByRegionIdentifier = new HashMap<String, Set<String>>();
        for (final PrivateProperty privateProperty : privateProperties) {
            for (int chunkX = privateProperty.boundTopLeft.x; chunkX <= privateProperty.boundBottomRight.x; chunkX++) {
                for (int chunkZ = privateProperty.boundTopLeft.z; chunkZ <= privateProperty.boundBottomRight.z; chunkZ++) {
                    final String regionIdentifier = RegionFile.getIdentifierForChunk(chunkX, chunkZ);
                    Set<String> regionChunks = chunksByRegionIdentifier.get(regionIdentifier);
                    if (regionChunks == null) {
                        regionChunks = new HashSet<String>();
                        chunksByRegionIdentifier.put(regionIdentifier, regionChunks);
                    }
                    regionChunks.add(RegionFile.getChunkIdentifier(chunkX, chunkZ));
                }
            }
        }
        return chunksByRegionIdentifier;
    }
}
