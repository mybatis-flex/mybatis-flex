/**
 * Copyright (c) 2022-2023, Mybatis-Flex (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mybatisflex.core.audit;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * 默认的审计消息创建器，用来创建带有 hostIp 的审计消息
 */
public class DefaultMessageFactory implements MessageFactory {

    private final String platform = "mybatis-flex";
    private final String hostIp = getHostIp();


    @Override
    public AuditMessage create() {
        AuditMessage message = new AuditMessage();
        message.setPlatform(platform);
        message.setHostIp(hostIp);
        return message;
    }


    private static String getHostIp() {
        try {
            for (Enumeration<NetworkInterface> net = NetworkInterface.getNetworkInterfaces(); net.hasMoreElements(); ) {
                NetworkInterface networkInterface = net.nextElement();
                if (networkInterface.isLoopback() || networkInterface.isVirtual() || !networkInterface.isUp()) {
                    continue;
                }
                for (Enumeration<InetAddress> addrs = networkInterface.getInetAddresses(); addrs.hasMoreElements(); ) {
                    InetAddress addr = addrs.nextElement();
                    if (addr instanceof Inet4Address) {
                        return addr.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            // ignore
        }
        return "127.0.0.1";
    }
}
