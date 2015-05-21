/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.airlift.http.client.jetty;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import org.eclipse.jetty.client.api.ContentProvider;

import javax.annotation.concurrent.GuardedBy;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SingleBufferContent
    implements ContentProvider
{
    private final byte[] data;

    @GuardedBy("buffers")
    private final List<ByteBuffer> buffers = new ArrayList<>();

    public SingleBufferContent(byte[] data)
    {
        this.data = data;
    }

    public List<ByteBuffer> getBuffers()
    {
        synchronized (buffers) {
            return ImmutableList.copyOf(buffers);
        }
    }

    @Override
    public long getLength()
    {
        return data.length;
    }

    @Override
    public Iterator<ByteBuffer> iterator()
    {
        ByteBuffer result = ByteBuffer.wrap(data);
        synchronized (buffers) {
            buffers.add(result);
        }

        return Iterators.forArray(result);
    }
}
