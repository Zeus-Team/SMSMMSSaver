package me.akulakovsky.josyko_test.models;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class Message {

    public static final String TYPE_SMS = "sms";
    public static final String TYPE_MMS = "mms";

    private static final String KEY_ID = "id";
    private static final String KEY_TYPE = "type";
    private static final String KEY_BOX = "box";
    private static final String KEY_SENDER = "from_sender";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_MESSAGES_ARRAY = "messages";

    private static final Uri SMS_URI = Uri.parse("content://sms");
    private static final Uri MMS_URI = Uri.parse("content://mms");

    private int id; //id in android database
    private String type; // sms or mms
    private String box; // inbox or outbox
    private String sender; //sender phone number
    private String message; //message body

    public Message(int id, String type, String box, String sender, String message) {
        this.id = id;
        this.type = type;
        this.box = box;
        this.sender = sender;
        this.message = message;
    }

    /**
     * Generates request json with all messages available on the device
     * @param context
     * @return
     */
    public static JSONObject generateRequest(Context context) {
        JSONObject request = null;

        List<Message> messages = getMessages(context);

        if (messages != null && messages.size() > 0) {
            request = new JSONObject();
            JSONArray jsonArray = new JSONArray();

            try {
                for (Message msg: messages) {
                    JSONObject msgJson = new JSONObject();
                    msgJson.put(KEY_ID, msg.getId());
                    msgJson.put(KEY_TYPE, msg.getType());
                    msgJson.put(KEY_BOX, msg.getBox());
                    msgJson.put(KEY_SENDER, msg.getSender());
                    msgJson.put(KEY_MESSAGE, msg.getMessage());
                    jsonArray.put(msgJson);
                }
                request.put(KEY_MESSAGES_ARRAY, jsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return request;
    }

    /**
     * Gets all messages available on the device
     * @param context
     * @return list of messages
     */
    private static List<Message> getMessages(Context context) {
        List<Message> messageList = new ArrayList<Message>();
        //part 1. getting all sms:
        Cursor smsCursor = context.getContentResolver().query(SMS_URI, null, null, null, null);
        if (smsCursor != null) {
            while (smsCursor.moveToNext()) {
                int id = smsCursor.getInt(smsCursor.getColumnIndex("_id"));
                String phone = smsCursor.getString(smsCursor.getColumnIndex("address"));
                String body = smsCursor.getString(smsCursor.getColumnIndex("body"));
                String box = smsCursor.getString(smsCursor.getColumnIndex("type"));
                messageList.add(new Message(id, TYPE_SMS, box, phone, body));
                break;// Stop reading oll of my messages
            }
            smsCursor.close();
        }

        //part 2. getting all mms:
        Cursor mmsCursor = context.getContentResolver().query(MMS_URI, null, null, null, null);
        if (mmsCursor != null) {
            while (mmsCursor.moveToNext()) {
//                int id = mmsCursor.getInt(mmsCursor.getColumnIndex("_id"));
//                String phone = MmsUtils.getAddressNumber(context, id);
//                String body = MmsUtils.getMmsText(context, id);
//                //String box = mmsCursor.getString(mmsCursor.getColumnIndex("type"));
//                messageList.add(new Message(id, TYPE_MMS, "0", phone, body));
                break;// Stop reading oll of my messages
            }
            mmsCursor.close();
        }

        return messageList;
    }

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public String getBox() {
        return box;
    }
}
