/*
 * Created by admin on 17/12/2017
 * Last modified 10:49 17/12/17
 */

package com.example.admin.myapplication.driveControl;

import android.support.annotation.NonNull;

import com.example.admin.myapplication.common.Messages.MotionMessage;

import java.io.IOException;

import businessLogic.serialization.interfaces.ITypedSerializer;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: com.example.admin.myapplication.driveControl.</P>
 * <P></P>
 */

class DrivingCommandSerializer implements ITypedSerializer<MotionMessage> {

    /*
                                      up/down         left/right                                                    can be 0!
    command\byte    |   0           |   1           |   2         |   3          |   4           |   5           |   6        |   7          |
    ----------------|---------------|---------------|-------------|--------------|---------------|---------------|------------|--------------|
    stand           |  66 h = 102   |  80 h = 128   |  80 h = 128 |  7F h = 127  |  7E h = 126   |  00           |  00        |  99 h = 153  |
    forward         |  66           |  58 h = 88    |  80         |  7F          |  7E           |  00           |  24        |  99          |
    backward        |  66           |  A7 h = 167   |  80         |  7F          |  7E           |  00           |  27        |  99          |
    right           |  66           |  80           |  A8 h = 168 |  7F          |  7E           |  00           |  2A        |  99          |
    left            |  66           |  80           |  59 h = 89  |  7F          |  7E           |  00           |  D9        |  99          |
     */

    @Override
    public byte[] serialize(MotionMessage motionMessage) throws IOException {
        double angleInRadians = Math.toRadians(motionMessage.Angle);
        int power = motionMessage.Power;

        byte forward_backward, right_left;

        if (power == 0) {
            forward_backward = right_left = (byte) 128;
        } else {
            long y = Math.round(128 - power * Math.sin(angleInRadians) * 0.4);

            if (y > 167) {
                y = 167;
            }

            long x = Math.round(128 + power * Math.cos(angleInRadians) * 0.4);
            if (x < 89) {
                x = 89;
            }

            forward_backward = (byte) y;
            right_left = (byte) x;
        }

        byte[] serializedData = new byte[8];

        serializedData[0] = 102;
        serializedData[1] = forward_backward;
        serializedData[2] = right_left;
        serializedData[3] = 127;
        serializedData[4] = 126;
        serializedData[5] = 0;
        serializedData[6] = 0;
        serializedData[7] = (byte) 153;
        return serializedData;
    }

    @Override
    public void serialize(MotionMessage motionMessage, byte[] dataBuffer, int startIndex) throws IOException {
        byte[] bytes = this.serialize(motionMessage);
        int bufferSize = dataBuffer.length - startIndex;
        if (bufferSize < bytes.length) {
            String msg = "The buffer is not big enough to the serialized data. buffer empty size: " + bufferSize + " , serialized data size: " + bytes.length;
            throw new IOException(msg);
        }
        System.arraycopy(bytes, 0, dataBuffer, startIndex, bytes.length);

    }

    @NonNull
    private static String bytesToHex(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder((bytes.length + 1) * 2);
        for (byte aByte : bytes) {
            stringBuilder.append(byteToHex(aByte));
            stringBuilder.append(' ');
        }
        return stringBuilder.toString();
    }

    @NonNull
    private static String byteToHex(byte aByte) {
        final char[] hexArray = "0123456789ABCDEF".toCharArray();
        int v = aByte & 0xFF;
        return String.valueOf(hexArray[v >>> 4]) +
                hexArray[v & 0x0F];
    }
}
