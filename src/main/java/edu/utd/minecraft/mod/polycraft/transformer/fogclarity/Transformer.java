package edu.utd.minecraft.mod.polycraft.transformer.fogclarity;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;

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

public class Transformer implements IClassTransformer
{
	private final String classNameEntityRenderer = "blt";
	private String classNameEntityLivingBase = "sv";
	private String setupFogMethodName = "a";
	private final String targetMethodDesc = "(IF)V";
	private final int instructionToRemoveStart = 266; //looking for opcode 18 in decimal with next var value = 3
	private final int instructionsToRemove = 7;

	@Override
	public byte[] transform(String name, String newName, byte[] bytes)
	{
		//System.out.println("transforming: "+name);
		if (name.equals(classNameEntityRenderer))
		{
			return handleWorldTransform(bytes);
		}
		else if (name.equals("net.minecraft.client.renderer.EntityRenderer")) // MCP testing
		{
			setupFogMethodName = "setupFog";
			classNameEntityLivingBase = "net/minecraft/entity/EntityLivingBase";
			return handleWorldTransform(bytes);
		}

		return bytes;
	}

	private byte[] handleWorldTransform(byte[] bytes)
	{
		System.out.println("**************** Fog clarity transform running on EntityRenderer *********************** ");
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);

		// find method to inject into
		Iterator<MethodNode> methods = classNode.methods.iterator();
		while (methods.hasNext())
		{
			MethodNode m = methods.next();
			if (m.name.equals(setupFogMethodName) && m.desc.equals(targetMethodDesc))
			{
				System.out.println("In target method! Patching!");
				
//				for (int temp1 = 265; temp1 <280; temp1++)
//				{
//					System.out.print("Instruction " + temp1 +": ");
//					System.out.println(m.instructions.get(temp1).getOpcode());
//					
//				}
				

				AbstractInsnNode currentNode = m.instructions.get(instructionToRemoveStart);
				for (int i = 0; i < instructionsToRemove; i++) {
					AbstractInsnNode nextNode = currentNode.getNext();
					m.instructions.remove(currentNode);
					currentNode = nextNode;
				}

				// make new instruction list
				InsnList toInject = new InsnList();
				toInject.add(new VarInsnNode(ALOAD, 3));
				toInject.add(new MethodInsnNode(INVOKESTATIC, FogClarity.class.getCanonicalName().replaceAll(Pattern.quote("."), "/"), "getDensityWater", "(L" + classNameEntityLivingBase + ";)F"));

				// inject new instruction list into method instruction list
				m.instructions.insertBefore(currentNode, toInject);

//				System.out.println("-------");
//				for (int temp1 = 265; temp1 <280; temp1++)
//				{
//					System.out.print("Instruction " + temp1 +": ");
//					System.out.println(m.instructions.get(temp1).getOpcode());
//					
//				}
				System.out.println("Patching Complete!");
				break;
			}
		}

		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		classNode.accept(writer);
		return writer.toByteArray();
	}
}
