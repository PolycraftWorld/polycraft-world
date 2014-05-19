package edu.utd.minecraft.mod.polycraft.transformer.crafting;

import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

import java.util.Iterator;
import java.util.regex.Pattern;

import net.minecraft.launchwrapper.IClassTransformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

/*
 * Normal minecraft recipes aren't overloaded to allow different size item stacks to mean different things,
 * so the default behavior when more items of the same type are added to the same stack is to not recalculate
 * what the crafting output would be, but our extended recipe manager does allow this, so we need to make sure
 * that onCraftingMatrix is called when the addition happens (line 405 of Container).
 */
public class Transformer implements IClassTransformer {

	private String classNameContainer = "ye";
	private String classNameSlot = "zk";
	private String fieldNameSlotInventory = "f";
	private String fieldDescSlotInventory = "Lpo;";
	private String methodNameSlotClick = "a";
	private String methodDescSlotClick = "(IIILxl;)Labp;";
	private String methodNameOnCraftMatrixChanged = "a";
	private String methodDescOnCraftMatrixChanged = "(Lpo;)V";

	@Override
	public byte[] transform(String name, String newName, byte[] bytes) {
		if (name.equals(classNameContainer)) {
			return handleTransformContainer(bytes);
		}
		else if (name.equals("net.minecraft.inventory.Container")) {
			classNameContainer = name.replaceAll(Pattern.quote("."), "/");
			classNameSlot = "net/minecraft/inventory/Slot";
			fieldNameSlotInventory = "inventory";
			fieldDescSlotInventory = "Lnet/minecraft/inventory/IInventory;";
			methodNameSlotClick = "slotClick";
			methodDescSlotClick = "(IIILnet/minecraft/entity/player/EntityPlayer;)Lnet/minecraft/item/ItemStack;";
			methodNameOnCraftMatrixChanged = "onCraftMatrixChanged";
			methodDescOnCraftMatrixChanged = "(Lnet/minecraft/inventory/IInventory;)V";
			return handleTransformContainer(bytes);
		}
		return bytes;
	}

	private byte[] handleTransformContainer(byte[] bytes) {
		System.out.println("**************** Crafting transform running on Container ***********************");
		final ClassNode classNode = new ClassNode();
		final ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);

		Iterator<MethodNode> methods = classNode.methods.iterator();
		while (methods.hasNext()) {
			final MethodNode m = methods.next();
			if (m.name.equals(methodNameSlotClick) && m.desc.equals(methodDescSlotClick)) {
				System.out.println("In target method! Patching!");
				final InsnList toInject = new InsnList();
				toInject.add(new VarInsnNode(ALOAD, 0));
				toInject.add(new VarInsnNode(ALOAD, 9));
				toInject.add(new FieldInsnNode(GETFIELD, classNameSlot, fieldNameSlotInventory, fieldDescSlotInventory));
				toInject.add(new MethodInsnNode(INVOKEVIRTUAL, classNameContainer, methodNameOnCraftMatrixChanged, methodDescOnCraftMatrixChanged));
				// inject new instruction list into method instruction list
				m.instructions.insertBefore(m.instructions.get(843), toInject);

				System.out.println("Patching Complete!");
				break;
			}
		}

		final ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		classNode.accept(writer);
		return writer.toByteArray();
	}
}