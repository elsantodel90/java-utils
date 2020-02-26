/*
 * Copyright 2020 Agustín Santiago Gutiérrez
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package tk.elsantodel90.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

import com.google.common.collect.Iterators;

/**
 * A utility class allowing an InputStream or a Reader to be obtained by implementing only a much simpler
 * Iterator. The InputStream or Reader will provide the concatenation of all bytes or Strings provided by the iterator.
 * 
 * @author <a href=
 *         "https://github.com/elsantodel90/java-utils">https://github.com/elsantodel90/java-utils</a>
 *
 */
public class InputStreamUtils {

	// Public interface

	public static InputStream inputStreamFromByteArrayIterator(Iterator<byte[]> iter) {
		return new ByteArrayIteratorInputStream(iter);
	}

	public static InputStream inputStreamFromStringIterator(Iterator<String> iter, Charset charset) {
		return inputStreamFromByteArrayIterator(Iterators.transform(iter, s -> s.getBytes(charset)));
	}

	public static InputStream utf8StreamFromStringIterator(Iterator<String> iter) {
		return inputStreamFromStringIterator(iter, StandardCharsets.UTF_8);
	}

	public static Reader readerFromStringIterator(Iterator<String> iter) {
		return new InputStreamReader(utf8StreamFromStringIterator(iter), StandardCharsets.UTF_8);
	}

	// Internal implementation

	private InputStreamUtils() {
	}

	private static class ByteArrayIteratorInputStream extends InputStream {

		private final Iterator<byte[]> iter;

		public ByteArrayIteratorInputStream(Iterator<byte[]> iter) {
			this.iter = iter;
		}

		private byte[] nextArray = null;
		private int nextIndex = 0;

		private boolean ensureNext() {
			while (nextArray == null || nextArray.length <= nextIndex) {
				if (!iter.hasNext()) {
					return false;
				}
				nextArray = iter.next();
				nextIndex = 0;
			}
			return true;
		}

		@Override
		public int read() throws IOException {
			if (!ensureNext()) {
				return -1;
			}
			return nextArray[nextIndex++];
		}

		@Override
		public int read(byte[] b, int off, int len) throws IOException {
			if (!ensureNext()) {
				return -1;
			}
			int readSize = Math.min(nextArray.length - nextIndex, len);
			System.arraycopy(nextArray, nextIndex, b, off, readSize);
			nextIndex += readSize;
			return readSize;
		}

		@Override
		public int available() throws IOException {
			return nextArray == null ? 0 : nextArray.length - nextIndex;
		}
	}
}
