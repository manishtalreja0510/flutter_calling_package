import 'package:flutter/services.dart';

class FlutterCalling{

  static const platform = MethodChannel('com.audiocalling/connectwithcall');


  ///this function should be called when you want to connect a client with a call.
  ///[groupname] is the name of the group which you can use to have conference call
  ///[username] will be the username.
  ///[ Note ] username must be unique
  static void connectWithCall({required String username,required String groupname}) async{
    try{
      await platform.invokeMethod('connectWithCall',{"username":username,"groupname":groupname});
    }catch(e){
      print('Error in connecting with a call: $e');
    }
  }

  static Future<void> muteAudio()async{
    try{
      await platform.invokeMethod('muteAudio');
    } catch(e){
      print('error in  mute audio $e');
    }
  }
  static Future<void> unmuteAudio()async{
    try{
      await platform.invokeMethod('unmuteAudio');
    } catch(e){
      print('error in  Unmuting audio $e');
    }
  }

  static Future<void> endCall()async{
    try{
      await platform.invokeMethod('endCall');
    } catch(e){
      print('error in  Ending Call $e');
    }
  }
}