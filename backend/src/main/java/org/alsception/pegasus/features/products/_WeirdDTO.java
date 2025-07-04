package org.alsception.pegasus.features.products;

public class _WeirdDTO {
    public Object broken = new Object() {
        public String name = "test";
        public Object self = this; // circular reference
    };
}
