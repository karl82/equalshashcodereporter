package cz.rank.tests;

import org.junit.Test;

import java.util.Objects;

public class EqualsHashCodeReporterTest {
    @Test
    public void emptyEqualsAndHashCodeProduceNoReport() throws Exception {
        new EqualsHashCodeReporter(Object.class).report();
    }

    @Test(expected = AssertionError.class)
    public void onlyInHashCodeProducesException() throws Exception {
        new EqualsHashCodeReporter(OnlyHashCodeObject.class).report();
    }

    @Test(expected = AssertionError.class)
    public void onlyInEqualsProducesException() throws Exception {
        new EqualsHashCodeReporter(OnlyEqualsObject.class).report();
    }

    @Test(expected = AssertionError.class)
    public void differentFieldsProduceException() throws Exception {
        new EqualsHashCodeReporter(EqualsAndHashCodeDifferentObject.class).report();
    }

    private static class OnlyHashCodeObject {
        private final int field;

        private OnlyHashCodeObject(int field) {
            this.field = field;
        }

        @Override
        public int hashCode() {
            return Objects.hash(field);
        }
    }

    private static class OnlyEqualsObject {
        private final int field;

        private OnlyEqualsObject(int field) {
            this.field = field;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof OnlyEqualsObject)) return false;
            OnlyEqualsObject that = (OnlyEqualsObject) o;
            return field == that.field;
        }
    }

    private static class EqualsAndHashCodeDifferentObject {
        private final int field;
        private final int field2;

        private EqualsAndHashCodeDifferentObject(int field) {
            this.field = field;
            this.field2 = field << 2;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof EqualsAndHashCodeDifferentObject)) return false;
            EqualsAndHashCodeDifferentObject that = (EqualsAndHashCodeDifferentObject) o;
            return field == that.field;
        }

        @Override
        public int hashCode() {
            return Objects.hash(field, field2);
        }
    }
}
