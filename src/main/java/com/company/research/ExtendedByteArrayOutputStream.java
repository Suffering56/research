package com.company.research;


import javax.annotation.Nonnull;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Расширение {@link ByteArrayOutputStream} для работы с его буфером напрямую, что позволяет избегать добоплнительного копирования буфера и синхронизации.
 * ВАЖНО: потокобезопасность при работе отсутствует
 *
 * @author george_a
 * @version 27.10.13 8:04
 */
public class ExtendedByteArrayOutputStream extends ByteArrayOutputStream {

    public ExtendedByteArrayOutputStream() {
        super();
    }

    public ExtendedByteArrayOutputStream(int size) {
        super(size);
    }

    public byte [] getDataRef(){
        return buf;
    }

    @Override
    public int size() {
        return count;
    }

    @Nonnull
    public ByteArrayInputStream createInputStream() {
        return new ByteArrayInputStream(getDataRef(), 0, size());
    }
}
