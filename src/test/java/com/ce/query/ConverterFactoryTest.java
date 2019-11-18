package com.ce.query;


import com.ce.query.converter.ConverterFactory;
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
        ConverterFactory.INSTANCE.register(Object.class, String.class, ObjectToString.class);

        assertThat(ConverterFactory.INSTANCE.generate(Object.class, String.class)).isExactlyInstanceOf(ObjectToString.class);
    }

    @Test
    public void givenEmptyConverterFactory_whenGenerate_thenException() {
        ConverterFactory.INSTANCE.clear();
        assertThatThrownBy(() -> {
            ConverterFactory.INSTANCE.generate(String.class, Object.class);
        }).isExactlyInstanceOf(RuntimeException.class)
                .hasMessage("given source type String is not supported");
    }
}
