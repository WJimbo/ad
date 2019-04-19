package com.yunchuan.tingyanwu.ad.service.socket;

public class CommandMessageData {
    /**
     * 机器ID
     */
    private String mId;

    /**
     * 消息ID
     */
    private String msgId;

    /**
     * 操作类型（命令）
     */
    private String commond;

    /**
     * 需要转换的类型（暂无用）
     */
    private String converType;

    /**
     * 数据json对象（暂无用）
     */
    private String content;

    /**
     * 当前连接的唯一标识
     */
    private String token;

    /**
     * 状态,0：未读，1：已读 2：重发
     */
    private Integer status = 0;

    /**
     * 消息发送时间
     */
    private Long createTime;

    /**
     * 参数
     */
    private String param;


    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getCommond() {
        return commond;
    }

    public void setCommond(String commond) {
        this.commond = commond;
    }

    public String getConverType() {
        return converType;
    }

    public void setConverType(String converType) {
        this.converType = converType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }
}
