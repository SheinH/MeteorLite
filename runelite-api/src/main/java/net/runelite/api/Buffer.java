/*
 * Copyright (c) 2020, Noodleeater <noodleeater4@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.api;

/**
 * Represents a byte buffer
 */
public interface Buffer extends Node {

  byte[] getPayload();

  int getOffset();

  /**
   * Use this api to write to byte buffers
   */

  void writeIntME2$api(int var1);

  void writeByte$api(int var1);

  void writeByteA$api(int var1);

  void writeByteB$api(int var1);

  void writeByteC$api(int var1);

  void writeByteB0$api(int var1);

  void writeIntME3$api(int var1);

  void write1$api(int var1);

  void write2$api(int var1);

  void writeByte01$api(int var1);

  void writeShort$api(int var1);

  void writeMedium$api(int var1);

  void writeByte0A1$api(int var1);

  void writeByte2$api(int var1);

  void writeByte01A$api(int var1);

  void writeIntME$api(int var1);

  void writeInt2$api(int var1);

  void writeInt$api(int var1);

  void writeLong$api(long var1);

  void writeShort01$api(int var1);

  void writeInt0123$api(int var1);

  void writeShortA$api(int var1);

  void writeShort01A$api(int var1);

  void writeStringCp1252NullTerminated$api(String string);

  /**
   * Use this api to write to byte buffers
   */
  void writeByte(int var1);

  void writeShort(int var1);

  void writeInt(int var1);

  void writeLong(long var1);
}
