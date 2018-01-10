/*
 * Created by admin on 16/10/2017
 * Last modified 11:43 16/10/17
 */

package com.example.admin.myapplication.common;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: com.example.admin.myapplication.common.</P>
 * <P></P>
 */

public final class Enums {

    public enum Fragments {
        Video,
        Settings
    }

    public enum MapOperation {
        DOP_ADD,
        DOP_DELETE,
        DOP_UPDATE,
        DOP_NONE
    }

    public enum MapType {
        DCL_WALL,
        DCL_CEILING,
        DCL_SUBTLE,// high noise
        DCL_NONE
    }

}
