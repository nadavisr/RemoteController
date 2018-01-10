/*
 * Created by admin on  27/09/2017
 * Last modified 11:23 27/09/17
 */

package com.example.admin.myapplication.video;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.admin.myapplication.common.Messages.ImageMessage;

import businessLogic.common.interfaces.IHandler;
import businessLogic.graph.executors.interfaces.ITransformExecutor;
import services.common.ServicesBaseBgOperation;

/**
 * <P>Project: RemoteController.</P>
 * <P>Package: com.example.admin.myapplication.video</P>
 * <P></P>
 */

public class DecoderExecutor extends ServicesBaseBgOperation implements ITransformExecutor<ImageMessage, Bitmap> {

    private IHandler<Exception> m_exceptionHandler;
    private IHandler<Bitmap> m_sourceHandler;

    @Override
    public void setInput(ImageMessage imageMessage) {
        if(imageMessage == null){
            m_logger.warning("Image message received null.");
            return;
        }

        byte [] imBytes = imageMessage.Data;

        try {
            Bitmap bitmap = BitmapFactory.decodeByteArray(imBytes, 0, imBytes.length);
            m_sourceHandler.setInput(bitmap);
        }
        catch (Exception ex){
            m_logger.warning("An error occurred while trying to decode the ImageMessage",ex);
            m_exceptionHandler.setInput(ex);
        }
    }

    @Override
    public void setExceptionHandler(IHandler<Exception> exceptionHandler) {
        m_exceptionHandler = exceptionHandler;
    }

    @Override
    public void setSourceHandler(IHandler<Bitmap> sourceHandler) {
        m_sourceHandler = sourceHandler;
    }

    @Override
    protected void internalInitialize() throws Exception {
        //No internal logic
    }

    @Override
    protected void internalStart() throws Exception {
        if (m_sourceHandler == null) {
            String msg = "IHandler<Bitmap> is null.";
            m_logger.error(msg);
            throw new NullPointerException(msg);
        }
        if (m_exceptionHandler == null) {
            String msg = "IHandler<Exception> is null.";
            m_logger.error(msg);
            throw new NullPointerException(msg);
        }
    }

    @Override
    protected void internalStop() throws Exception {
        //No internal logic.
    }

    @Override
    protected void innerDispose() throws Exception {
        //No internal logic.
    }
}
