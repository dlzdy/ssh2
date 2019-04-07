/**
 *
    --------------------- 
    作者：九风萍舟 
    来源：CSDN 
    原文：https://blog.csdn.net/u012954072/article/details/52957097 
    版权声明：本文为博主原创文章，转载请附上博文链接！ 
 */
package com.jeenms.ssh2;

import java.io.InputStream;
import java.io.OutputStream;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

/**
 *
 * @author zhangdy
 * @date 2019年4月7日 下午11:52:56
 */
public class DD {
    final static String host = "192.168.1.1"; // 服务器的ip地址
    
    final static String user = "root"; // 服务器的账号
    
    final static String password = "root"; // 服务器的密码
    
    /**
     * @Title: exectueShellCommand
     * @Description: 执行shell命令，返回得到的结果
     * @param @param
     *        command 执行的shell命令
     * @param @return
     *        shell命令的返回值
     */
    public static String exectueShellCommand(String command) {
        String executeResultString = new String();
        try {
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            JSch jsch = new JSch();
            Session session = jsch.getSession(user, host, 22);
            session.setPassword(password);
            session.setConfig(config);
            session.connect();
            System.out.println("Connected");
            // create the excution channel over the session
            Channel channel = session.openChannel("exec");
            // Set the command that you want to execute
            // In our case its the remote shell script
            ((ChannelExec)channel).setCommand(command);
            channel.setInputStream(null);
            ((ChannelExec)channel).setErrStream(System.err);
            // Gets an InputStream for this channel. All data arriving in as messages from the remote side can be
            InputStream in = channel.getInputStream();
            OutputStream out = channel.getOutputStream();
            // Execute the command
            channel.connect();
            
            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0)
                        break;
                    executeResultString = new String(tmp, 0, i); // 获取命令执行的返回值，结果是多行的也存在一个String中
                }
                
                if (channel.isClosed()) {
                    System.out.println("exit-status: " + channel.getExitStatus());
                    break;
                }
                
                try {
                    Thread.sleep(1000);
                }
                catch (Exception ee) {
                } // 让线程执行1秒
            }
            channel.disconnect();
            session.disconnect();
            System.out.println("DONE");
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        System.out.println("return values is:" + executeResultString);
        return executeResultString;
    }
    public static void main(String[] args) {
        exectueShellCommand("enable;root");
    }
}
