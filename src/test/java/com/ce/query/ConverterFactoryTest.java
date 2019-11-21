package com.ce.query;


import com.ce.query.converter.DataConverterManager;
import com.ce.query.converter.IDataConverter;
import com.ce.query.exception.ConvertException;
import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.assertThatThrownBy;

public class ConverterFactoryTest {

    class Bean {

        public String name;
        public int age;

        public Bean(String name, int age) {
            this.name = name;
            this.age = age;
        }
    }

    class BeanDataConverter implements IDataConverter<Bean> {
        @Override
        public Bean fromRaw(Object o) {
            return new Bean("test", 10);
        }

        @Override
        public Object toRaw(Bean bean) {
            return bean;
        }
    }

    @Test
    public void givenRegisteredConverter_whenGet_thenTypeMatch() {
        DataConverterManager.INSTANCE.register(Bean.class, new BeanDataConverter());

        assertThat(DataConverterManager.INSTANCE.lookup(Bean.class)).isExactlyInstanceOf(BeanDataConverter.class);
    }

    @Test
    public void givenRegisteredConverter_whenGetAndConverter_thenOk() {
        DataConverterManager.INSTANCE.register(Bean.class, new BeanDataConverter());

        assertThat(DataConverterManager.INSTANCE.lookup(Bean.class)).isExactlyInstanceOf(BeanDataConverter.class);

        IDataConverter<Bean> converter = DataConverterManager.INSTANCE.lookup(Bean.class);

        Bean bean = converter.fromRaw(null);
        assertThat(bean.name).isEqualToIgnoringCase("test");

        Object o = converter.toRaw(bean);
        assertThat(o).isExactlyInstanceOf(Bean.class);
    }

    class NonExist {
    }

    @Test
    public void givenNotSupportedSourceType_whenGenerate_thenException() {
        assertThatThrownBy(() -> {
            DataConverterManager.INSTANCE.lookup(NonExist.class);
        }).isExactlyInstanceOf(ConvertException.class)
                .hasMessage("given data type NonExist is not supported");
    }
}
