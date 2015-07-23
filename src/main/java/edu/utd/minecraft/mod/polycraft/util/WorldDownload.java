package edu.utd.minecraft.mod.polycraft.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.microsoft.azure.storage.blob.ListBlobItem;

public class WorldDownload {

    public static void main(String[] args) throws IOException, URISyntaxException, InvalidKeyException, StorageException {
        int arg = 0;
        final String worldDir = args[arg++];
        final String worldRegionDir = RegionFile.getDirFromWorld(worldDir);
        final String storageAccountName = args[arg++];
        final String storageAccountKey = args[arg++];
        final String storageContainerName = args[arg];
        final CloudStorageAccount storageAccount = CloudStorageAccount.parse(String.format("DefaultEndpointsProtocol=http;AccountName=%s;AccountKey=%s", storageAccountName, storageAccountKey));
        final CloudBlobClient serviceClient = storageAccount.createCloudBlobClient();
        final CloudBlobContainer storageContainer = serviceClient.getContainerReference(storageContainerName);

        long downloadCount = 0;
        long downloadBytes = 0;
        for (final ListBlobItem trunkRegionFileListBlobItem : storageContainer.listBlobs()) {
            if (trunkRegionFileListBlobItem instanceof CloudBlockBlob) {
                final CloudBlockBlob regionFileBlob = (CloudBlockBlob)trunkRegionFileListBlobItem;
                final ByteArrayOutputStream regionFileOutputStream = new ByteArrayOutputStream();
                System.out.println(String.format("Downloading region file: %s", regionFileBlob.getUri().toString()));
                regionFileBlob.download(regionFileOutputStream);
                byte[] regionFileBytes = regionFileOutputStream.toByteArray();
                new RegionFile(new File(String.format("%s%sr.%s.mca", worldRegionDir, File.separator, regionFileBlob.getName().replace(',', '.')))).write(regionFileBytes);
                downloadBytes += regionFileBytes.length;
                downloadCount++;
            }
        }

        if (downloadCount > 0)
            System.out.println(String.format("%d region files downloaded (%s)", downloadCount, Format.humanReadableByteCount(downloadBytes, false)));
    }
}
