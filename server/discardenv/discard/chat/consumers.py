# chat/consumers.py
from asgiref.sync import async_to_sync
from channels.generic.websocket import WebsocketConsumer
from channels.layers import get_channel_layer
from .ChannelLayerBattleRoyalerManager import BattleRoyalManager
import discard.modelsT as model
import discard.scripts.MassConversationManager as mc
import discard.chat_settings as s
import json
import datetime
import time
import threading

class ChatConsumer(WebsocketConsumer):
    def connect(self):
        self.room_name = self.scope['url_route']['kwargs']['room_name']
        self.room_group_name = 'chat_%s' % self.room_name
        self.user_group = str(self.scope['account'].account_pk)

        # Join room group
        conversation = self.scope['conversation']
        c_pk = self.room_name
       
        
        print(str(conversation))

        async_to_sync(self.channel_layer.group_add)(
            self.user_group,
            self.channel_name
        )

        async_to_sync(self.channel_layer.group_add)(
             self.room_group_name,
             self.channel_name
        )

        #check if is the same for all rooms or only for one 
        if hasattr(self.channel_layer,'xD') is False:
            self.channel_layer.xD = {}

        else:
            print("Not initialization")

        if  c_pk not in self.channel_layer.xD.keys():
            self.channel_layer.xD[c_pk] = BattleRoyalManager(
                self.channel_layer, 
                self.room_group_name,
                self.channel_name,
                self.room_name)
            t = threading.Timer(50, self.channel_layer.xD[c_pk].start_battle_royale)
            t.start()


        self.channel_layer.xD[c_pk].add_consumer_account(
            self.scope['account'], 
            (self.user_group, self.channel_name)
            )

        d = self.channel_layer.xD[c_pk].get_time_before_start(conversation)

        async_to_sync(self.channel_layer.group_send)(
            self.user_group, 
            {
                'type': 'start_message',
                'message': "Alooo, ms "+self.scope['account'].username,
                'time' : str(d)
            })

        print([a.username for a in self.channel_layer.xD[c_pk].consumer_accounts])


        self.accept()

    def disconnect(self, close_code):
        # Leave room group

        async_to_sync(self.channel_layer.group_discard)(
            self.room_group_name,
            self.channel_name
        )

    def can_send(self):
        self.started = True









    # Receive message from WebSocket
    def receive(self, text_data):
        if self.scope['account'] not in self.channel_layer.xD[self.room_name].consumer_accounts:
            self.disconnect('22')
            mc.change_user_state(self.room_name,self.scope['account'])


        text_data_json = json.loads(text_data)
        
        
        if 'vote' in text_data_json.keys():
            voted_user = text_data_json['vote']
            self.channel_layer.xD[self.room_name].addVote(self.scope['account'], voted_user)
            return
        if 'sender' in text_data_json.keys():
            sender  = text_data_json['sender']
        else:
            sender = "server"

        message = text_data_json['message']
        # Send message to room group
        channel_layer = get_channel_layer()

        if  self.channel_layer.xD[self.room_name].started is True:
            async_to_sync(self.channel_layer.group_send)(
                self.room_group_name,
                {
                    'type':   'chat_message',
                    'message': message,
                    'sender':  sender
                }
            )
        else:
            async_to_sync(self.channel_layer.group_send)(
                self.user_group,
                {
                    'type': 'chat_warning',
                    'message': "CONVERSATION DIDNT START YET"
                }
            )
       











    # Receive message from room group
    def chat_message(self, event):
        message = event['message']
        sender  = event['sender']
        # Send message to WebSocket
        self.send(text_data=json.dumps({
            'type': 'chat_message',
            'sender': sender,
            'message': message
        }))

    def chat_warning(self, event):
        message = event['message']

        self.send(text_data=json.dumps({
            'type': 'warning',
            'message': message
        }))

    def start_message(self, event):
        message = event['message']
        time = event['time']
        # Send message to WebSocket
        self.send(text_data=json.dumps({
            'type': 'countdown',
            'message': message,
            'time': time
        }))

    def send_usernames(self, event):
        usernames = event['usernames']
        # Send message to WebSocket
        self.send(text_data=json.dumps({
            'type': 'usernames',
            #todo remove this message, added only for anroid functionality sustain
            'message': str(usernames),
            'usernames': usernames
        }))

    def inform_about_kick(self, event):
        username = event['username']
        print("SENDING SAD NEWS")
        # Send message to WebSocket
        self.send(text_data=json.dumps({
            'type': 'info_about_kick',
            #todo remove this message, added only for anroid functionality sustain
            'message': str(username) + ', you just lost',
            'usernames': username
        }))
        self.disconnect('32')

    
    def voting_send(self, event):
        message = event['message']
        removed = event['removed']
        # Send message to WebSocket
        self.send(text_data=json.dumps({
            'type': 'voting_status',
            'message': message,
            'removed': removed
        }))


    def finish_send(self, event):
        message = event['message']
        # Send message to WebSocket
        self.send(text_data=json.dumps({
            'type': 'finish_message',
            'message': message
        }))

        self.scope['conversation'].finished = True
        self.scope['conversation'].save()

        #self.channel_layer.xD[self.room_name] 


    # def send_info_about_discarted_user_(self, event):
    #     message = event['message']
    #     s = event['start']
    #     # Send message to WebSocket
    #     self.send(text_data=json.dumps({
    #         'type': 'discared_info',
    #         'discarted': message,
    #     }))
