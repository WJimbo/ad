package com.yunchuan.tingyanwu.ad.util;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

import java.nio.charset.Charset;

public class CustomProtocalCodecFactory  implements ProtocolCodecFactory {
	
	private final CustomProtocalEncoder encoder;  
    private final CustomProtocolDecoder decoder;  
      
    public CustomProtocalCodecFactory(Charset charset) {
        encoder=new CustomProtocalEncoder(charset);  
        decoder=new CustomProtocolDecoder(charset);  
    }  

    @Override
	public ProtocolDecoder getDecoder(IoSession arg0) throws Exception {
		return decoder;
	}

	@Override
	public ProtocolEncoder getEncoder(IoSession arg0) throws Exception {
		return encoder;
	}

}
