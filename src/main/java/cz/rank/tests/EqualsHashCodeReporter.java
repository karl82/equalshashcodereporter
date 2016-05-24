package cz.rank.tests;

import org.hamcrest.CoreMatchers;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertThat;
import static org.objectweb.asm.Opcodes.ASM5;

public class EqualsHashCodeReporter {

    private final EqualsAndHashCodeVisitor equalsAndHashCodeVisitor = new EqualsAndHashCodeVisitor();
    private final Class<?> klass;

    public EqualsHashCodeReporter(Class<?> klass) throws IOException {
        this.klass = klass;
    }

    public void report() throws IOException {
        new ClassReader(this.klass.getName()).accept(equalsAndHashCodeVisitor, 0);
        doReport(equalsAndHashCodeVisitor);
    }

    private void doReport(EqualsAndHashCodeVisitor equalsAndHashCodeVisitor) {
        assertThat(equalsAndHashCodeVisitor.equalsFields, CoreMatchers.is(equalsAndHashCodeVisitor.hashCodeFields));
    }

    private static class EqualsAndHashCodeVisitor extends ClassVisitor {
        private final Set<String> equalsFields = new HashSet<>();
        private final Set<String> hashCodeFields = new HashSet<>();

        public EqualsAndHashCodeVisitor() {
            super(ASM5);
        }

        @Override
        public String toString() {
            return "EqualsAndHashCodeVisitor{" +
                    "equalsFields=" + equalsFields +
                    ", hashCodeFields=" + hashCodeFields +
                    '}';
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            switch (name) {
                case "equals":
                    return new NonStaticNonTransientFieldsInMethodVisitor(equalsFields);
                case "hashCode":
                    return new NonStaticNonTransientFieldsInMethodVisitor(hashCodeFields);

            }
            return super.visitMethod(access, name, desc, signature, exceptions);
        }

        private static class NonStaticNonTransientFieldsInMethodVisitor extends MethodVisitor {
            private final Set<String> fields;

            public NonStaticNonTransientFieldsInMethodVisitor(Set<String> fields) {
                super(ASM5);
                this.fields = fields;
            }

            @Override
            public void visitFieldInsn(int opcode, String owner, String name, String desc) {
                switch (opcode) {
                    case Opcodes.GETFIELD:
                        fields.add(name);
                }
            }
        }
    }
}
