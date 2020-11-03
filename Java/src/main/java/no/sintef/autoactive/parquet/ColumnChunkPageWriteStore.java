/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package no.sintef.autoactive.parquet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.NotImplementedException;
import org.apache.parquet.bytes.ByteBufferAllocator;
import org.apache.parquet.bytes.BytesInput;
import org.apache.parquet.column.ColumnDescriptor;
import org.apache.parquet.column.Encoding;
import org.apache.parquet.column.page.DataPageV1;
import org.apache.parquet.column.page.DictionaryPage;
import org.apache.parquet.column.page.PageWriteStore;
import org.apache.parquet.column.page.PageWriter;
import org.apache.parquet.column.statistics.Statistics;
import org.apache.parquet.hadoop.CodecFactory.BytesCompressor;
import org.apache.parquet.hadoop.ParquetFileWriter;
import org.apache.parquet.io.ParquetEncodingException;
import org.apache.parquet.schema.MessageType;

public class ColumnChunkPageWriteStore implements PageWriteStore {
	//private static final Logger LOG = LoggerFactory.getLogger(ColumnChunkPageWriteStore.class);

	//private static ParquetMetadataConverter parquetMetadataConverter = new ParquetMetadataConverter();

	private static final class ColumnChunkPageWriter implements PageWriter {

		private final ColumnDescriptor path;
		private final BytesCompressor compressor;

		//private final ByteArrayOutputStream tempOutputStream = new ByteArrayOutputStream();
		//private final ConcatenatingByteArrayCollector buf;
		private DictionaryPage dictionaryPage;

		//private long uncompressedLength;
		//private long compressedLength;
		private long totalValueCount;
		//private int pageCount;

		// repetition and definition level encodings are used only for v1 pages and don't change
		//private Set<Encoding> rlEncodings = new HashSet<Encoding>();
		//private Set<Encoding> dlEncodings = new HashSet<Encoding>();
		//private List<Encoding> dataEncodings = new ArrayList<Encoding>();

		//private Statistics totalStatistics;
		//private final ByteBufferAllocator allocator;
		private List<DataPageV1> dataPages = new ArrayList<DataPageV1>();

		private ColumnChunkPageWriter(ColumnDescriptor path,
				BytesCompressor compressor,
				ByteBufferAllocator allocator) {
			this.path = path;
			this.compressor = compressor;
			//this.allocator = allocator;
			//this.buf = new ConcatenatingByteArrayCollector();
		}

		public void writePage(BytesInput bytes,
				int valueCount,
				Statistics statistics,
				Encoding rlEncoding,
				Encoding dlEncoding,
				Encoding valuesEncoding) throws IOException {
			long uncompressedSize = bytes.size();
			if (uncompressedSize > Integer.MAX_VALUE) {
				throw new ParquetEncodingException(
						"Cannot write page larger than Integer.MAX_VALUE bytes: " +
								uncompressedSize);
			}
			BytesInput compressedBytes = compressor.compress(bytes);
			dataPages.add(new DataPageV1(BytesInput.copy(compressedBytes), valueCount, (int)uncompressedSize, statistics, rlEncoding, dlEncoding, valuesEncoding));
			totalValueCount += valueCount;
			/*
			long compressedSize = compressedBytes.size();
			if (compressedSize > Integer.MAX_VALUE) {
				throw new ParquetEncodingException(
						"Cannot write compressed page larger than Integer.MAX_VALUE bytes: "
								+ compressedSize);
			}
			tempOutputStream.reset();
			parquetMetadataConverter.writeDataPageHeader(
					(int)uncompressedSize,
					(int)compressedSize,
					valueCount,
					statistics,
					rlEncoding,
					dlEncoding,
					valuesEncoding,
					tempOutputStream);
			this.uncompressedLength += uncompressedSize;
			this.compressedLength += compressedSize;
			this.totalValueCount += valueCount;
			this.pageCount += 1;

			// Copying the statistics if it is not initialized yet so we have the correct typed one
			if (totalStatistics == null) {
				totalStatistics = statistics.copy();
			} else {
				totalStatistics.mergeStatistics(statistics);
			}

			// by concatenating before collecting instead of collecting twice,
			// we only allocate one buffer to copy into instead of multiple.
			buf.collect(BytesInput.concat(BytesInput.from(tempOutputStream), compressedBytes));
			rlEncodings.add(rlEncoding);
			dlEncodings.add(dlEncoding);
			dataEncodings.add(valuesEncoding);
			*/
		}

		public void writePageV2(
				int rowCount, int nullCount, int valueCount,
				BytesInput repetitionLevels, BytesInput definitionLevels,
				Encoding dataEncoding, BytesInput data,
				Statistics<?> statistics) throws IOException {
			throw new NotImplementedException();
			/*
			int rlByteLength = toIntWithCheck(repetitionLevels.size());
			int dlByteLength = toIntWithCheck(definitionLevels.size());
			int uncompressedSize = toIntWithCheck(
					data.size() + repetitionLevels.size() + definitionLevels.size()
					);
			// TODO: decide if we compress
			BytesInput compressedData = compressor.compress(data);
			int compressedSize = toIntWithCheck(
					compressedData.size() + repetitionLevels.size() + definitionLevels.size()
					);
			tempOutputStream.reset();
			parquetMetadataConverter.writeDataPageV2Header(
					uncompressedSize, compressedSize,
					valueCount, nullCount, rowCount,
					statistics,
					dataEncoding,
					rlByteLength,
					dlByteLength,
					tempOutputStream);
			this.uncompressedLength += uncompressedSize;
			this.compressedLength += compressedSize;
			this.totalValueCount += valueCount;
			this.pageCount += 1;

			// Copying the statistics if it is not initialized yet so we have the correct typed one
			if (totalStatistics == null) {
				totalStatistics = statistics.copy();
			} else {
				totalStatistics.mergeStatistics(statistics);
			}

			// by concatenating before collecting instead of collecting twice,
			// we only allocate one buffer to copy into instead of multiple.
			buf.collect(
					BytesInput.concat(
							BytesInput.from(tempOutputStream),
							repetitionLevels,
							definitionLevels,
							compressedData)
					);
			dataEncodings.add(dataEncoding);
			*/
		}
		
		/*
		private int toIntWithCheck(long size) {
			if (size > Integer.MAX_VALUE) {
				throw new ParquetEncodingException(
						"Cannot write page larger than " + Integer.MAX_VALUE + " bytes: " +
								size);
			}
			return (int)size;
		}
		*/

		public long getMemSize() {
			throw new NotImplementedException("ColumnChunkPageWriterStore.getMemSize()");
		}

		public void writeToFileWriter(ParquetFileWriter writer) throws IOException {
			writer.startColumn(path, totalValueCount, compressor.getCodecName());
			if (dictionaryPage != null) {
				writer.writeDictionaryPage(dictionaryPage);
				// tracking the dictionary encoding is handled in writeDictionaryPage
			}
			
			for (DataPageV1 dataPage : dataPages) {
				writer.writeDataPage(dataPage.getValueCount(),
						dataPage.getUncompressedSize(),
						dataPage.getBytes(), 
						dataPage.getStatistics(),
						dataPage.getRlEncoding(),
						dataPage.getDlEncoding(),
						dataPage.getValueEncoding());
			}
			writer.endColumn();
			
			dataPages.clear();
			totalValueCount = 0;
			
			/*
			writer.writeDataPages(buf, uncompressedLength, compressedLength, totalStatistics,
					rlEncodings, dlEncodings, dataEncodings);
			writer.endColumn();
			if (LOG.isDebugEnabled()) {
				LOG.debug(
						String.format(
								"written %,dB for %s: %,d values, %,dB raw, %,dB comp, %d pages, encodings: %s",
								buf.size(), path, totalValueCount, uncompressedLength, compressedLength, pageCount, new HashSet<Encoding>(dataEncodings))
						+ (dictionaryPage != null ? String.format(
								", dic { %,d entries, %,dB raw, %,dB comp}",
								dictionaryPage.getDictionarySize(), dictionaryPage.getUncompressedSize(), dictionaryPage.getDictionarySize())
								: ""));
			}
			rlEncodings.clear();
			dlEncodings.clear();
			dataEncodings.clear();
			pageCount = 0;
			*/
		}

		public long allocatedSize() {
			throw new NotImplementedException("ColumnChunkPageWriterStore.allocatedSize()");
		}

		public void writeDictionaryPage(DictionaryPage dictionaryPage) throws IOException {
			if (this.dictionaryPage != null) {
				throw new ParquetEncodingException("Only one dictionary page is allowed");
			}
			BytesInput dictionaryBytes = dictionaryPage.getBytes();
			int uncompressedSize = (int)dictionaryBytes.size();
			BytesInput compressedBytes = compressor.compress(dictionaryBytes);
			this.dictionaryPage = new DictionaryPage(BytesInput.copy(compressedBytes), uncompressedSize, dictionaryPage.getDictionarySize(), dictionaryPage.getEncoding());
		}

		public String memUsageString(String prefix) {
			//return buf.memUsageString(prefix + " ColumnChunkPageWriter");
			throw new NotImplementedException("ColumnChunkPageWriterStore.memUsageString(...)");
		}

	}

	private final Map<ColumnDescriptor, ColumnChunkPageWriter> writers = new HashMap<ColumnDescriptor, ColumnChunkPageWriter>();
	private final MessageType schema;

	public ColumnChunkPageWriteStore(BytesCompressor compressor, MessageType schema, ByteBufferAllocator allocator) {
		this.schema = schema;
		for (ColumnDescriptor path : schema.getColumns()) {
			writers.put(path,  new ColumnChunkPageWriter(path, compressor, allocator));
		}
	}

	public PageWriter getPageWriter(ColumnDescriptor path) {
		return writers.get(path);
	}

	public void flushToFileWriter(ParquetFileWriter writer) throws IOException {
		for (ColumnDescriptor path : schema.getColumns()) {
			ColumnChunkPageWriter pageWriter = writers.get(path);
			pageWriter.writeToFileWriter(writer);
		}
	}

}