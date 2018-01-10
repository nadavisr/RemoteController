/*
 * Created by admin on  27/09/2017
 * Last modified 11:23 27/09/17
 */

package com.example.admin.myapplication.common;

import org.msgpack.annotation.Message;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: com.example.admin.myapplication.common.</P>
 * <P></P>
 */

public final class Messages {

    @Message
    public static class Header {
        public long TimeStamp;
        public byte SenderID;
        public byte DestinationID;
        public short IncMsgNum;
        public short MessageType;
        public short CheckSum;
        public short MessageSize;
    }

    @Message
    public static class ImageMessage {
        public short Width;
        public short Height;
        public byte[] Data;
    }

    @Message
    public static class MotionMessage {
        public int Angle;
        public int Power;

        @Override
        public String toString() {
            return "[Angle=" + Angle + ",Power=" + Power + "]";
        }
    }

    @Message
    public static class MapMessage {

        public float LocationX; // Robot x location
        public float LocationY; // Robot y location
        public float Angle;     //Robot angle from x axis
        public float ReceptionIntensity;
        public boolean ExposedFlag;
        public float AmbientLightIntensity;
        public float SlopeAngle;
        public long LandmarkId;
        public Enums.MapOperation OperationOnLandmark;
        public float LandmarkSegmentStartX;
        public float LandmarkSegmentEndX;
        public float LandmarkSegmentStartY;
        public float LandmarkSegmentEndY;
        public float LandmarkSegmentEllipseA;
        public float LandmarkSegmentEllipseB;
        public float LandmarkSegmentEllipseCenterX;
        public float LandmarkSegmentEllipseCenterY;
        public float LandmarkSegmentEllipseTheta;
        public float LandmarkSegmentEllipseMinT;
        public float LandmarkSegmentEllipseMaxT;
        public Enums.MapType LandmarkClass;

        @Override
        public String toString() {
            return "MapMessage{" +
                    "LocationX=" + LocationX +
                    ", LocationY=" + LocationY +
                    ", Angle=" + Angle +
                    ", ReceptionIntensity=" + ReceptionIntensity +
                    ", ExposedFlag=" + ExposedFlag +
                    ", AmbientLightIntensity=" + AmbientLightIntensity +
                    ", SlopeAngle=" + SlopeAngle +
                    ", LandmarkId=" + LandmarkId +
                    ", OperationOnLandmark=" + OperationOnLandmark +
                    ", LandmarkSegmentStartX=" + LandmarkSegmentStartX +
                    ", LandmarkSegmentEndX=" + LandmarkSegmentEndX +
                    ", LandmarkSegmentStartY=" + LandmarkSegmentStartY +
                    ", LandmarkSegmentEndY=" + LandmarkSegmentEndY +
                    ", LandmarkSegmentEllipseA=" + LandmarkSegmentEllipseA +
                    ", LandmarkSegmentEllipseB=" + LandmarkSegmentEllipseB +
                    ", LandmarkSegmentEllipseCenterX=" + LandmarkSegmentEllipseCenterX +
                    ", LandmarkSegmentEllipseCenterY=" + LandmarkSegmentEllipseCenterY +
                    ", LandmarkSegmentEllipseTheta=" + LandmarkSegmentEllipseTheta +
                    ", LandmarkSegmentEllipseMinT=" + LandmarkSegmentEllipseMinT +
                    ", LandmarkSegmentEllipseMaxT=" + LandmarkSegmentEllipseMaxT +
                    ", LandmarkClass=" + LandmarkClass +
                    '}';
        }
    }
}
