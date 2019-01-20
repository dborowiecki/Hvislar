# chat/consumers.py
from asgiref.sync import async_to_sync
from channels.generic.websocket import WebsocketConsumer
from channels.layers import get_channel_layer
from .ChannelLayerBattleRoyalerManager import BattleRoyalManager
import discard.modelsT as model
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
        self.started = False

        # Join room group
        conversation = self.scope['conversation']
       
        d = self.get_time_before_start()
        print(str(conversation))

        async_to_sync(self.channel_layer.group_add)(
            self.user_group,
            self.channel_name
        )

        #MA WYSYŁAĆ UŻYTKOWNIKOWI CZAS POZOSTAŁY DO ROZPOCZĘCIA:
        async_to_sync(self.channel_layer.group_send)(
            self.user_group, 
            {
                'type': 'start_message',
                'message': "Alooo, ms User",
                'time' : str(d)
            })

        async_to_sync(self.channel_layer.group_add)(
             self.room_group_name,
             self.channel_name
        )

        #check if is the same for all rooms or only for one 
        if hasattr(self.channel_layer,'xD') is False:
            print("SHOULD START VOTE")
            #TODO: SETUP METHOD SHOULD DEFINE TIME AND AFTER COUNTDOWN SEND TO ALL USERS
            #ANOTHER LIST OF ANOTHER USERS, 
            #self.setup()
            self.channel_layer.xD = {}
            #should wait 10 seconds and then run battle royale
          
        else:
            print("Not initialization")

        
        self.channel_layer.xD[self.room_name] = BattleRoyalManager(
                self.channel_layer, 
                self.room_group_name,
                self.channel_name,
                self.room_name)
        if hasattr(self.channel_layer.xD, self.room_name) is False:
            t = threading.Timer(5, self.channel_layer.xD[self.room_name].start_battle_royale)
            t.start()


        self.channel_layer.xD[self.room_name].add_consumer_account(
            self.scope['account'], 
            (self.user_group, self.channel_name)
            )
        self.accept()

    def disconnect(self, close_code):
        # Leave room group
        print('HELOOOOOO')
        self.channel_layer.xD[self.room_name].remove_consumer_accounts([self.scope['account'].username])
        async_to_sync(self.channel_layer.group_discard)(
            self.room_group_name,
            self.channel_name
        )

    # Receive message from WebSocket
    def receive(self, text_data):
        text_data_json = json.loads(text_data)
        message = text_data_json['message']

        if hasattr(self.channel_layer,'vote') is True:
            voted_user = text_data_json['vote']
            self.channel_layer.xD[self.room_name].addVote(self.scope['account'], voted_user)


        # Send message to room group
        channel_layer = get_channel_layer()
        print(str(channel_layer))
        if self.started:
            async_to_sync(self.channel_layer.group_send)(
                self.room_group_name,
                {
                    'type': 'chat_message',
                    'message': message
                }
            )
        else:
            self.get_time_before_start()

    # Receive message from room group
    def chat_message(self, event):
        message = event['message']

        # Send message to WebSocket
        self.send(text_data=json.dumps({
            'type': 'chat_message',
            'sender': self.scope['account'].username,
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

        # Send message to WebSocket
        self.send(text_data=json.dumps({
            'type': 'info_about_kick',
            #todo remove this message, added only for anroid functionality sustain
            'message': str(username) + ' kick',
            'usernames': username
        }))

    def get_time_before_start(self):
        print('1')
        if self.started:
            return 0
        else:
            print('2')
            conv = self.scope['conversation']
            #a = datetime.datetime.now()
            #time.sleep(3)
            #a = a + datetime.timedelta(seconds = s.TIME_TO_CLOSE_CONVERSATION.second)
            a = conv.creation_date + datetime.timedelta(seconds = s.TIME_TO_CLOSE_CONVERSATION.second)
            now = datetime.datetime.now()
            diff =  a - now 
            print('3')
           
            if diff.days < 0:
                d = 0
                self.started = True
            else:
                d = diff

            print('4')
            return d
    
    def voting_send(self, event):
        message = event['message']
        removed = event['removed']
        # Send message to WebSocket
        self.send(text_data=json.dumps({
            'type': 'voting_status',
            'message': message,
            'removed': removed
        }))

    # def send_info_about_discarted_user_(self, event):
    #     message = event['message']
    #     s = event['start']
    #     # Send message to WebSocket
    #     self.send(text_data=json.dumps({
    #         'type': 'discared_info',
    #         'discarted': message,
    #     }))
