package org.lifehelper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.BotInvitedJoinGroupRequestEvent;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.NewFriendRequestEvent;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.utils.ExternalResource;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
public final class LifeHelper extends JavaPlugin
{
    public static final LifeHelper INSTANCE = new LifeHelper();
    private static File adminUser;
    private static File userData;
    private static File groupData;
    private static long admin;
    private static ArrayList<String> userList;
    private static ArrayList<String> groupList;

    private LifeHelper()
    {
        super(new JvmPluginDescriptionBuilder("org.lifehelper.LifeHelper", "1.0").name("LifeHelper").info("为生活增添便利").author("Conan").build());
    }

    public void init()
    {
        adminUser = LifeHelper.INSTANCE.resolveDataFile("admin.txt");
        userData = LifeHelper.INSTANCE.resolveDataFile("userData.txt");
        groupData = LifeHelper.INSTANCE.resolveDataFile("groupData.txt");
        userList = new ArrayList<>();
        groupList = new ArrayList<>();
        try
        {
            readAdminUser();
            readDataToList(userData, userList);
            readDataToList(groupData, groupList);
        }
        catch(IOException e)
        {
            e.printStackTrace();
            getLogger().info("读取数据失败！");
        }
    }

    public void readAdminUser() throws  IOException
    {
        FileInputStream fileInputStream = new FileInputStream(adminUser);
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String text;
        text = bufferedReader.readLine();
        admin = Long.parseLong(text);
    }

    private void readDataToList(File data, ArrayList<String> list) throws IOException
    {
        FileInputStream fileInputStream = new FileInputStream(data);
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String text;
        while((text = bufferedReader.readLine()) != null)
        {
            list.add(text);
        }
        bufferedReader.close();
        inputStreamReader.close();
        fileInputStream.close();
    }

    private void writeListToFile(ArrayList<String> list, File data) throws IOException
    {
        FileOutputStream fileOutputStream = new FileOutputStream(data);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
        for(String s : list)
            outputStreamWriter.write(s + "\n");
        outputStreamWriter.close();
        fileOutputStream.close();
    }

    public void addUser(String userID)
    {
        userList.add(userID);
    }

    public void addGoup(String groupID)
    {
        groupList.add(groupID);
    }

    public String getJson() throws IOException
    {
        StringBuilder json = new StringBuilder();
        String url = "https://store-site-backend-static.ak.epicgames.com/freeGamesPromotions?locale=zh-CN&country=CN&allowCountries=CN";
        URL urls = new URL(url);
        java.net.HttpURLConnection conn = (java.net.HttpURLConnection) urls.openConnection();
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.75 Safari/537.36");
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setRequestMethod("GET");
        //设置编码格式为UTF-8
        conn.setRequestProperty("contentType", "UTF-8");
        InputStream inputStream = conn.getInputStream();
        try(BufferedReader bf = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)))
        {
            String line;
            while((line = bf.readLine()) != null) json.append(line);
        }
        inputStream.close();
        return json.toString();
    }

    public void SendPushData(String str, Group group, Friend friend)
    {
        JSONObject jsonObject = JSON.parseObject(str);
        JSONObject data = jsonObject.getJSONObject("data");
        JSONObject catalog = data.getJSONObject("Catalog");
        JSONObject searchStore = catalog.getJSONObject("searchStore");
        JSONArray elements = searchStore.getJSONArray("elements");
        for(int i = 0; i<elements.size(); i++)
        {
            String s = elements.getString(i);
            JSONObject json = JSON.parseObject(s);
            String game_title = json.getString("title");
            String game_description = json.getString("description");
            String game_seller = JSON.parseObject(json.getString("seller")).getString("name");
            JSONObject price = JSON.parseObject(json.getString("price"));
            JSONObject totalPrice = JSON.parseObject(price.getString("totalPrice"));
            JSONObject fmtPrice = JSON.parseObject(totalPrice.getString("fmtPrice"));
            String game_originalprice = fmtPrice.getString("originalPrice");
            String game_discountPrice = fmtPrice.getString("discountPrice");
            JSONArray keyImages = json.getJSONArray("keyImages");
            JSONObject keyImage = JSON.parseObject(keyImages.getString(0));
            String game_keyimages = keyImage.getString("url");
            if(game_discountPrice.equals("0") && !game_originalprice.equals("0"))
            {
                JSONObject promotions = json.getJSONObject("promotions");
                JSONArray promotionalOffers = promotions.getJSONArray("promotionalOffers");
                JSONObject promotionalOffer = JSON.parseObject(promotionalOffers.getString(0));
                JSONArray promotionalOffers1 = promotionalOffer.getJSONArray("promotionalOffers");
                JSONObject promotionalOffer1 = JSON.parseObject(promotionalOffers1.getString(0));
                String game_startdate = promotionalOffer1.getString("startDate");
                game_startdate = game_startdate.split("\\.")[0];
                String game_enddate = promotionalOffer1.getString("endDate");
                game_enddate = game_enddate.split("\\.")[0];

                String result = "游戏名:" + game_title + "\n描述:" + game_description + "\n发售商:" + game_seller + "\n\n原价:" + game_originalprice + "\n现价:" + game_discountPrice + "\n\n白嫖开始日期:" + game_startdate + "\n白嫖结束日期:" + game_enddate + "\n\n快去 Epic Game 领取吧~";
                try
                {
                    URL url = new URL(game_keyimages);
                    URLConnection uc = url.openConnection();
                    InputStream in = uc.getInputStream();
                    byte[] bytes = in.readAllBytes();
                    ExternalResource er = ExternalResource.create(bytes);
                    if(friend != null)
                    {
                        Image image = net.mamoe.mirai.contact.Contact.uploadImage(friend, er);
                        MessageChain mc = new MessageChainBuilder()
                                .append(image)
                                .append(result)
                                .build();
                        Bot bot = Bot.getInstances().get(0);
                        Objects.requireNonNull(bot.getFriend(friend.getId())).sendMessage(mc);
                    }
                    if(group != null)
                    {
                        Image image = net.mamoe.mirai.contact.Contact.uploadImage(group, er);
                        MessageChain mc = new MessageChainBuilder()
                                .append(image)
                                .append(result)
                                .build();
                        Bot bot = Bot.getInstances().get(0);
                        Objects.requireNonNull(bot.getGroup(group.getId())).sendMessage(mc);
                    }
                    er.close();
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }

            }
        }
    }

    @Override
    public void onEnable()
    {
        getLogger().info("LifeHelper LifeHelper loaded!");

        init();
        try
        {
            Timer timer = new Timer();
            long weekTime = 24*60*60*1000*7;
            final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-08 '19:00:00'");
            Date startTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(sdf.format(new Date()));
            if(System.currentTimeMillis() > startTime.getTime())
            {
                startTime = new Date(startTime.getTime() + weekTime);
            }
            timer.scheduleAtFixedRate(new TimerTask(){
                @Override
                public void run()
                {
                    try
                    {
                        String json = getJson();
                        Bot bot = Bot.getInstances().get(0);
                        for(String s : userList)
                        {
                            SendPushData(json, null, bot.getFriend(Long.parseLong(s)));
                        }
                        for(String s : groupList)
                        {
                            SendPushData(json, bot.getGroup(Long.parseLong(s)), null);
                        }
                    }
                    catch(Exception e)
                    {
                        getLogger().info(e);
                    }

                }
            }, startTime, 24*60*60*1000*7);
        }
        catch(ParseException e)
        {
            e.printStackTrace();
        }

        // 输出文件名
        GlobalEventChannel.INSTANCE.subscribeAlways(GroupMessageEvent.class, this::printFileName);

        // 好友白嫖推送
        GlobalEventChannel.INSTANCE.subscribeAlways(FriendMessageEvent.class, this::UserPush);

        // 群组白嫖推送
        GlobalEventChannel.INSTANCE.subscribeAlways(GroupMessageEvent.class, this::GroupPush);

        // 触发群组推送
        GlobalEventChannel.INSTANCE.subscribeAlways(GroupMessageEvent.class, this::SendPushToGroup);

        // 触发好友推送
        GlobalEventChannel.INSTANCE.subscribeAlways(FriendMessageEvent.class, this::SendPushToFriend);

        // 自动同意添加好友
        GlobalEventChannel.INSTANCE.subscribeAlways(NewFriendRequestEvent.class, this::AcceptFriend);

        // 自动同意被邀入群
        GlobalEventChannel.INSTANCE.subscribeAlways(BotInvitedJoinGroupRequestEvent.class, this::AcceptGroup);

        // 转发消息给作者
        GlobalEventChannel.INSTANCE.subscribeAlways(FriendMessageEvent.class, this::SendMessage);

    }

    private void SendMessage(@NotNull FriendMessageEvent event)
    {
        Bot bot = Bot.getInstances().get(0);
        MessageChain mc = new MessageChainBuilder()
                .append(String.valueOf(event.getSender().getId()))
                .append("\n").append(event.getSender().getNick()).append(":\n\t\t")
                .append(event.getMessage())
                .build();
        Objects.requireNonNull(bot.getFriend(admin)).sendMessage(mc);
    }

    private void AcceptFriend(@NotNull NewFriendRequestEvent event)
    {
        event.accept();
        Bot bot = Bot.getInstances().get(0);
        Objects.requireNonNull(bot.getFriend(admin)).sendMessage("QQ " + event.getFromId() + " 添加了 Robot");
    }

    private void AcceptGroup(@NotNull BotInvitedJoinGroupRequestEvent event)
    {
        event.accept();
        Bot bot = Bot.getInstances().get(0);
        Objects.requireNonNull(bot.getFriend(admin)).sendMessage("群组 " + event.getGroupId() + " 邀请了 Robot");
    }

    private void printFileName(@NotNull GroupMessageEvent event)
    {
        if(event.getMessage().contentToString().contains("[文件]"))
        {
            String temp = event.getMessage().contentToString();
            String fileName = temp.replace("[文件]", "");
            event.getGroup().sendMessage(fileName);
        }
    }

    private void UserPush(@NotNull FriendMessageEvent event)
    {
        if(event.getMessage().contentToString().equals("开启白嫖推送"))
        {
            String id = String.valueOf(event.getSender().getId());
            if(userList.contains(id))
            {
                event.getSender().sendMessage("当前已开启白嫖推送，无需重复开启");
                return;
            }
            addUser(id);
            try
            {
                writeListToFile(userList, userData);
                event.getSender().sendMessage("定时推送已开启，每周五晚定时推送");
            }
            catch(IOException e)
            {
                e.printStackTrace();
                getLogger().info("fail to writeUser");
            }
        }

        else if(event.getMessage().contentToString().equals("关闭白嫖推送"))
        {
            String id = String.valueOf(event.getSender().getId());
            if(userList.contains(id))
            {
                userList.remove(id);
                try
                {
                    writeListToFile(userList, userData);
                    event.getSender().sendMessage("定时推送已关闭");
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }
            }
            else event.getSender().sendMessage("当前未开启推送！");
        }
    }

    private void GroupPush(@NotNull GroupMessageEvent event)
    {
        if(event.getMessage().contentToString().equals("开启白嫖推送"))
        {
            String id = String.valueOf(event.getGroup().getId());
            if(groupList.contains(id))
            {
                event.getGroup().sendMessage("当前已开启白嫖推送，无需重复开启");
                return;
            }
            addGoup(id);
            try
            {
                writeListToFile(groupList, groupData);
                event.getGroup().sendMessage("定时推送已开启，每周五晚定时推送");
            }
            catch(IOException e)
            {
                e.printStackTrace();
                getLogger().info("fail to writeGroup");
            }
        }

        else if(event.getMessage().contentToString().equals("关闭白嫖推送"))
        {
            String id = String.valueOf(event.getGroup().getId());
            if(groupList.contains(id))
            {
                groupList.remove(id);
                try
                {
                    writeListToFile(groupList, groupData);
                    event.getGroup().sendMessage("定时推送已关闭");
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }
            }
            else event.getGroup().sendMessage("当前未开启推送！");
        }
    }

    private void SendPushToGroup(@NotNull GroupMessageEvent event)
    {
        if(event.getMessage().contentToString().equals("白嫖"))
        {
            try
            {
                SendPushData(getJson(), event.getGroup(), null);
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void SendPushToFriend(@NotNull FriendMessageEvent event)
    {
        if(event.getMessage().contentToString().equals("白嫖"))
        {
            try
            {
                SendPushData(getJson(), null, event.getFriend());
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}