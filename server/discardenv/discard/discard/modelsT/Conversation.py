from django.db import models
from django.template.defaultfilters import slugify
from django.contrib.auth.models import User
from . import *

class Conversation(models.Model):
    conversation_pk = models.AutoField(primary_key=True)

    '''
    timestamp_from is later date than timestamp_to
    '''
    def get_messages_from_conversation(self, **kwargs):
        time_stamp_from    = kwargs['time_from']
        time_stamp_to      = kwargs['time_to']
        number_of_messages = kwargs['number_of_messages']

        messages = Message.objects.filter(conversation_fk=self)

        if messages is None:
            return None
        '''TODO: timestamp method requires tests'''
        if number_of_messages is None:
            if time_stamp_from or time_stamp_to is None:
                raise ValueError('You should define number of messages or both timestamps')
            else:
                messages = messages.filter(
                    send_time__gt = time_stamp_to).filter(
                    send_time__lt = time_stamp_from)
                return messages

        elif time_stamp_from is not None:
            messages = messages.filter(send_time__lt = time_stamp_from)

            if time_stamp_to is not None:
                if time_stamp_from < time_stamp_to:
                    raise ValueError('time_from should be later than time_to')
                messages =messages.filter(send_time__gt = time_stamp_to)


        messages = messages[:number_of_messages]
        return messages
        #TODO extend request for time period or sth

    class Meta:
        db_table = '"conversation"'
