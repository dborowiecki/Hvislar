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

        if hasattr(self.channel_layer,'xD') is False:
            print("SHOULD START VOTE")
            self.channel_layer.xD = BattleRoyalManager(self.channel_layer, self.room_group_name)
        else:
            print("SHOULD START VOTE1")
            self.channel_layer.xD.change_vote_state()

        self.accept()

    def disconnect(self, close_code):
        # Leave room group
        async_to_sync(self.channel_layer.group_discard)(
            self.room_group_name,
            self.channel_name
        )

    # Receive message from WebSocket
    def receive(self, text_data):
        text_data_json = json.loads(text_data)
        message = text_data_json['message']

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
            'message': message
        }))

    def start_message(self, event):
        message = event['message']
        time = event['time']
        # Send message to WebSocket
        self.send(text_data=json.dumps({
            'message': message,
            'time': time
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
        s = event['start']
        # Send message to WebSocket
        self.send(text_data=json.dumps({
            'message': message,
            'start': s
        }))
