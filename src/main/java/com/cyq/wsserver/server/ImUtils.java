package com.cyq.wsserver.server;

import com.alibaba.fastjson.JSON;
import com.cyq.wsserver.common.ImPacket;
import com.cyq.wsserver.common.ImSessionContext;
import com.cyq.wsserver.common.WsConsts;
import com.cyq.ppc.strategy.NormalRoundRobin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.tio.core.ChannelContext;
import org.tio.core.Tio;
import org.tio.utils.lock.SetWithLock;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class ImUtils implements InitializingBean {

    private static NormalRoundRobin<ChannelContext> sdkRoundRobin = new NormalRoundRobin();
    private static NormalRoundRobin<ChannelContext> blackGroupRoundRobin = new NormalRoundRobin();
    private static NormalRoundRobin<ChannelContext> tbxjhbRoundRobin = new NormalRoundRobin();
    private static NormalRoundRobin<ChannelContext> tbxjhbGenQrRoundRobin = new NormalRoundRobin();
    private static NormalRoundRobin<ChannelContext> tbxjhbPlusRoundRobin = new NormalRoundRobin();

    private static ImUtils INSTANCE;

    @Override
    public void afterPropertiesSet() throws Exception {
        ImUtils.INSTANCE = this;
    }

    public static void addSdkConnection(ChannelContext channelContext) {
        sdkRoundRobin.getList().add(channelContext);
    }


    public static void removeSdkConnection(ChannelContext channelContext) {
        List<ChannelContext> list = sdkRoundRobin.getList();
        Iterator<ChannelContext> iterator = list.iterator();
        while (iterator.hasNext()) {
            if (channelContext.getBsId().equals(iterator.next().getBsId())) {
                iterator.remove();;
            }
        }
    }

    public static void addBlackGroupConnection(ChannelContext channelContext) {
        blackGroupRoundRobin.getList().add(channelContext);
    }

    public static void removeBlackGroupConnection(ChannelContext channelContext) {
        List<ChannelContext> list = blackGroupRoundRobin.getList();
        Iterator<ChannelContext> iterator = list.iterator();
        while (iterator.hasNext()) {
            if (channelContext.getBsId().equals(iterator.next().getBsId())) {
                iterator.remove();;
            }
        }
    }

    public static void addTbxjhbConnection(ChannelContext channelContext) {
        tbxjhbRoundRobin.getList().add(channelContext);
    }

    public static void removeTbxjhbConnection(ChannelContext channelContext) {
        List<ChannelContext> list = tbxjhbRoundRobin.getList();
        Iterator<ChannelContext> iterator = list.iterator();
        while (iterator.hasNext()) {
            if (channelContext.getBsId().equals(iterator.next().getBsId())) {
                iterator.remove();;
            }
        }
    }

    public static void addTbxjhbGenQrConnection(ChannelContext channelContext) {
        tbxjhbGenQrRoundRobin.getList().add(channelContext);
    }

    public static void removeTbxjhbGenQrConnection(ChannelContext channelContext) {
        List<ChannelContext> list = tbxjhbGenQrRoundRobin.getList();
        Iterator<ChannelContext> iterator = list.iterator();
        while (iterator.hasNext()) {
            if (channelContext.getBsId().equals(iterator.next().getBsId())) {
                iterator.remove();;
            }
        }
    }


    public static void addTbxjhbPlusConnection(ChannelContext channelContext) {
        tbxjhbPlusRoundRobin.getList().add(channelContext);
    }

    public static void removeTbxjhbPlusConnection(ChannelContext channelContext) {
        List<ChannelContext> list = tbxjhbPlusRoundRobin.getList();
        Iterator<ChannelContext> iterator = list.iterator();
        while (iterator.hasNext()) {
            if (channelContext.getBsId().equals(iterator.next().getBsId())) {
                iterator.remove();;
            }
        }
    }


    public static void sendToSdkpay(ImPacket packet){
        ChannelContext next = sdkRoundRobin.next();
        if (Objects.isNull(next)) {
            log.error("没有 sdkpay socket 客户端在线！");
            return;
        }
        log.info("下发消息：{}", JSON.toJSONString(packet.getBodyObj()));
        Tio.send(next, packet);
    }

    public static void sendToBlackGroup(ImPacket packet){
        ChannelContext next = blackGroupRoundRobin.next();
        if (Objects.isNull(next)) {
            log.error("没有 淘宝小黑群 socket 客户端在线！");
            return;
        }
        log.info("下发消息：{}", JSON.toJSONString(packet.getBodyObj()));
        Tio.send(next, packet);
    }

    public static void sendToTbxjhb(ImPacket packet){
        ChannelContext next = tbxjhbRoundRobin.next();
        if (Objects.isNull(next)) {
            log.error("没有 淘宝现金红包 socket 客户端在线！");
            return;
        }
        log.info("下发消息：{}", JSON.toJSONString(packet.getBodyObj()));
        Tio.send(next, packet);
    }

    public static void sendToTbxjhbGenQr(ImPacket packet){
        ChannelContext next = tbxjhbGenQrRoundRobin.next();
        if (Objects.isNull(next)) {
            log.error("没有 淘宝现金红包产码 socket 客户端在线！");
            return;
        }
        log.info("下发消息：{}", JSON.toJSONString(packet.getBodyObj()));
        Tio.send(next, packet);
    }

    public static void sendToTbxjhbPlus(ImPacket packet){
        ChannelContext next = tbxjhbPlusRoundRobin.next();
        if (Objects.isNull(next)) {
            log.error("没有 淘宝现金红包PLUS产码 socket 客户端在线！");
            return;
        }
        log.info("下发消息：{}", JSON.toJSONString(packet.getBodyObj()));
        Tio.send(next, packet);
    }

    /**
     * 获取ImSessionContext
     * @param channelContext
     * @return
     * @author tanyaowu
     */
    public static ImSessionContext getImSessionContext(ChannelContext channelContext) {
        ImSessionContext imSessionContext = (ImSessionContext) channelContext.getAttribute(WsConsts.IM_SESSION_CONTEXT_KEY);
        return imSessionContext;
    }

    public static String getBsId(String account, String device) {
        return String.join("#", account, device);
    }

    /**
     * 判断用户是否在线
     *
     * @param userId
     * @return
     */
    public static boolean isOnline(String userId) {
        SetWithLock<ChannelContext> channelcontextSet = ImServerStarter.getServerTioConfigWs().users.find(ImServerStarter.getServerTioConfigWs(), userId);
        if (channelcontextSet != null && channelcontextSet.size() > 0) {
            return true;
        }
        return false;
    }


    /**
     * 获取在线用户数目
     *
     * @return
     */
    public static int getOnlineCount() {
        return ImServerStarter.getServerTioConfigWs().connections.size();
    }

    /**
     * 给指定 业务id 发消息
     *
     * @param bsId
     * @param packet
     */
    public static void sendToBsId(String bsId, ImPacket packet) {
        Tio.sendToBsId(ImServerStarter.getServerTioConfigWs(), bsId, packet);
    }

    /**
     * 给指定 channel 发消息
     *
     * @param channelContext
     * @param packet
     */
    public static void send(ChannelContext channelContext, ImPacket packet) {
        Tio.send(channelContext, packet);
    }

}
