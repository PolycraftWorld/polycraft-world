package edu.utd.minecraft.mod.polycraft.transformer.phaseshifter;

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

public class Transformer implements IClassTransformer {

	private String classNameItemRenderer = "bly";
	private String classNameBlock = "aji";
	private String classNameEntityPlayer = "yz"; //"yz" is the EntityPlayer, "bjk" is the EntityClientPlayerMP
	private String renderOverlaysMethodName = "b";
	private final String renderOverlaysMethodDesc = "(F)V";
	private final int lineToRemove = 51; //looking for opcode 182 in decimal

	@Override
	public byte[] transform(String name, String newName, byte[] bytes) {
		if (name.equals(classNameItemRenderer)) {
			return handleTransformItemRenderer(bytes);
		}
		else if (name.equals("net.minecraft.client.renderer.ItemRenderer")) {
			classNameItemRenderer = name.replaceAll(Pattern.quote("."), "/");
			classNameEntityPlayer = "net/minecraft/entity/player/EntityPlayer";
			classNameBlock = "net/minecraft/block/Block";
			renderOverlaysMethodName = "renderOverlays";
			return handleTransformItemRenderer(bytes);
		}
		return bytes;
	}

	private byte[] handleTransformItemRenderer(byte[] bytes) {
		System.out.println("**************** Phase shifter transform running on ItemRenderer ***********************");
		final ClassNode classNode = new ClassNode();
		final ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);

		Iterator<MethodNode> methods = classNode.methods.iterator();
		while (methods.hasNext()) {
			final MethodNode m = methods.next();
			if (m.name.equals(renderOverlaysMethodName) && m.desc.equals(renderOverlaysMethodDesc)) {
				System.out.println("In target method! Patching!");
				final AbstractInsnNode currentNode = m.instructions.get(lineToRemove);
				m.instructions.remove(currentNode);
				final InsnList toInject = new InsnList();
				toInject.add(new MethodInsnNode(INVOKESTATIC, PhaseShifter.class.getCanonicalName().replaceAll(Pattern.quote("."), "/"), "itemRendererIsEntityInsideOpaqueBlock", "(L" + classNameEntityPlayer + ";)Z"));
				// inject new instruction list into method instruction list
				m.instructions.insertBefore(m.instructions.get(lineToRemove), toInject);

				System.out.println("Patching Complete!");
				break;
			}
		}

		final ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		classNode.accept(writer);
		return writer.toByteArray();
	}
}