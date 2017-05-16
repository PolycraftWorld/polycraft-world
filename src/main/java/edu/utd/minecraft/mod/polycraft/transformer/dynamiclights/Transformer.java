package edu.utd.minecraft.mod.polycraft.transformer.dynamiclights;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.ISTORE;

import java.util.Iterator;
import java.util.regex.Pattern;

import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

/**
 * Magic happens in here. MAGIC. Obfuscated names will have to be updated with each Obfuscation change.
 * 
 */
public class Transformer implements IClassTransformer
{
	private final String classNameWorld = "ahb";
	private String classNameBlockAccess = "ahl";
	private String classNameBlock = "aji";
	private String computeLightValueMethodName = "a";
	private String targetMethodDesc = "(IIILahn;)I";

	@Override
	public byte[] transform(String name, String newName, byte[] bytes)
	{
		//System.out.println("transforming: "+name);
		if (name.equals(classNameWorld))
		{
			return handleWorldTransform(bytes);
		}
		else if (name.equals("net.minecraft.world.World")) // MCP testing
		{
			classNameBlockAccess = "net/minecraft/world/IBlockAccess";
			classNameBlock = "net/minecraft/block/Block";
			computeLightValueMethodName = "computeLightValue";
			targetMethodDesc = "(IIILnet/minecraft/world/EnumSkyBlock;)I";
			return handleWorldTransform(bytes);
		}

		return bytes;
	}

	private byte[] handleWorldTransform(byte[] bytes)
	{
		System.out.println("**************** Dynamic Lights transform running on World *********************** ");
		
		ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, 0);
        
        // find method to inject into
        Iterator<MethodNode> methods = classNode.methods.iterator();
        while(methods.hasNext())
        {
            MethodNode m = methods.next();
            if (m.name.equals(computeLightValueMethodName)
            && m.desc.equals(targetMethodDesc))
            {
                System.out.println("In target method! Patching!");
                
                AbstractInsnNode targetNode = null;
                Iterator<AbstractInsnNode> iter = m.instructions.iterator();
                boolean deleting = false;
                boolean replacing = false;
                while (iter.hasNext())
                {
                    targetNode = (AbstractInsnNode) iter.next();
                    
                    if (targetNode instanceof VarInsnNode)
                    {
                        VarInsnNode vNode = (VarInsnNode) targetNode;
                        if (vNode.var == 6)
                        {
                            if (vNode.getOpcode() == ASTORE)
                            {
                                System.out.println("Bytecode ASTORE 6 case!");
                                deleting = true;
                                continue;
                            }
                            else if (vNode.getOpcode() == ISTORE)
                            {
                                System.out.println("Bytecode ISTORE 6 case!");
                                replacing = true;
                                targetNode = (AbstractInsnNode) iter.next();
                                break;
                            }
                        }
                        
                        if (vNode.var == 7 && deleting)
                        {
                            break;
                        }
                    }
                    
                    if (deleting)
                    {
                        System.out.println("Removing "+targetNode);
                        iter.remove();
                    }
                }
                
                // make new instruction list
                InsnList toInject = new InsnList();
                
                // argument mapping! 0 is World, 5 is block, 123 are xyz
                toInject.add(new VarInsnNode(ALOAD, 0));
                toInject.add(new VarInsnNode(ALOAD, 5));
                toInject.add(new VarInsnNode(ILOAD, 1));
                toInject.add(new VarInsnNode(ILOAD, 2));
                toInject.add(new VarInsnNode(ILOAD, 3));
                toInject.add(new MethodInsnNode(INVOKESTATIC, DynamicLights.class.getCanonicalName().replaceAll(Pattern.quote("."), "/"), "getLightValue", "(L" + classNameBlockAccess + ";L" + classNameBlock + ";III)I"));
                
                if (replacing)
                {
                    toInject.add(new VarInsnNode(ISTORE, 6));
                }
                
                // inject new instruction list into method instruction list
                m.instructions.insertBefore(targetNode, toInject);
                
                System.out.println("Patching Complete!");
                break;
            }
        }
        
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        classNode.accept(writer);
        return writer.toByteArray();
	}
}
