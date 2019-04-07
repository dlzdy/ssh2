/**
 * --------------------- 
作者：一北 
来源：CSDN 
原文：https://blog.csdn.net/strommaybin/article/details/54354431 
版权声明：本文为博主原创文章，转载请附上博文链接！

https://www.cnblogs.com/fyzjhh/p/6634794.html
 */
package com.stormma.demo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Hashtable;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class Shell {
    //远程主机的ip地址
    private String ip;
    //远程主机登录用户名
    private String username;
    //远程主机的登录密码
    private String password;
    //设置ssh连接的远程端口
    public static final int DEFAULT_SSH_PORT = 22;  
    //保存输出内容的容器
    private ArrayList<String> stdout;

    /**
     * 初始化登录信息
     * @param ip
     * @param username
     * @param password
     */
    public Shell(final String ip, final String username, final String password) {
         this.ip = ip;
         this.username = username;
         this.password = password;
         stdout = new ArrayList<String>();
    }
    /**
     * 执行shell命令
     * @param command
     * @return
     */
    public int execute(final String command) {
        int returnCode = 0;
        JSch jsch = new JSch();
        MyUserInfo userInfo = new MyUserInfo();

        try {
            //创建session并且打开连接，因为创建session之后要主动打开连接
            
            Session session = jsch.getSession(username, ip, DEFAULT_SSH_PORT);
            session.setPassword(password);
            session.setUserInfo(userInfo);
            
            Hashtable<String, String> config = new Hashtable<String,String>();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            
            session.connect();

            //打开通道，设置通道类型，和执行的命令
            Channel channel = session.openChannel("exec");
            ChannelExec channelExec = (ChannelExec)channel;
            channelExec.setCommand(command);

            channelExec.setInputStream(null);
            BufferedReader input = new BufferedReader(new InputStreamReader
                    (channelExec.getInputStream()));

            channelExec.connect();
            System.out.println("The remote command is :" + command);

            //接收远程服务器执行命令的结果
            String line;
            while ((line = input.readLine()) != null) {  
                stdout.add(line);  
            }  
            input.close();  

            // 得到returnCode
            if (channelExec.isClosed()) {  
                returnCode = channelExec.getExitStatus();  
            }  

            // 关闭通道
            channelExec.disconnect();
            //关闭session
            session.disconnect();

        } catch (JSchException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnCode;
    }
    /**
     * get stdout
     * @return
     */
    public ArrayList<String> getStandardOutput() {
        return stdout;
    }

    public static void main(final String [] args) {  
//        Shell shell = new Shell("192.168.10.201", "root", "123456");
//        shell.execute("uname -s -r -v");
//        ArrayList<String> stdout = shell.getStandardOutput();
//        for (String str : stdout) {  
//            System.out.println(str);  
//        } 
        
          Shell shell = new Shell("192.168.1.1", "root", "root");
          shell.execute("help");
          ArrayList<String> stdout = shell.getStandardOutput();
          for (String str : stdout) {  
              System.out.println(str);  
          }          
//        shell.execute("enable");
//        stdout = shell.getStandardOutput();
//        for (String str : stdout) {  
//            System.out.println(str);  
//        }  
//        shell.execute("root");
//        for (String str : stdout) {  
//            System.out.println(str);  
//        }  


    }  
}
