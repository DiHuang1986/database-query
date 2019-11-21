package com.ce.query;


import com.ce.query.converter.ConverterManager;
import com.ce.query.converter.IConverter;
import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.assertThatThrownBy;

public class ConverterFactoryTest {

    public static class ObjectToString implements IConverter<Object, String> {
        @Override
        public String convert(Object o) {
            return o.toString();
        }
    }

    @Test
    public void givenRegisteredConverter_whenGenerate_thenTypeMatch() {
        ConverterManager.INSTANCE.register(Object.class, String.class, new ObjectToString());

        assertThat(ConverterManager.INSTANCE.generate(Object.class, String.class)).isExactlyInstanceOf(ObjectToString.class);
    }

    @Test
    public void givenNotSupportedSourceType_whenGenerate_thenException() {
        assertThatThrownBy(() -> {
            ConverterManager.INSTANCE.generate(String.class, Object.class);
        }).isExactlyInstanceOf(RuntimeException.class)
                .hasMessage("given source type String is not supported");
    }

    @Test
    public void givenNotSupportedTargetType_whenGenerate_thenException() {
        assertThatThrownBy(() -> {
            ConverterManager.INSTANCE.generate(Object.class, Object.class);
        }).isExactlyInstanceOf(RuntimeException.class)
                .hasMessage("given target type Object is not supported");
    }
}
