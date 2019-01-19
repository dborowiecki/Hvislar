from .Conversation import Conversation
from .Message import Message
class ConversationManager():

        def __init__(self, conversation):
            self.conversation = conversation

        def get_messages_from_conversation(self, **kwargs):
            """
            Method used for reciving messages from conversation between users.
            You need to put in **kwargs either number of messages you want to recive
            (then you recive messages from the newest) or both time stamps, then messages between
            time_from and time_to will be returned. If number of messages and both timestamps will be 
            in **kwargs then you recive required number of messages in timestamp period of time counted from 
            newest.

            Note
            ----
            time_from is later date than time_to

            Parameters
            --------
            **kwargs
            time_from: Date
                The date from which you want to get messages

            time_to: Date
                Date to which you want to recive informations

            number_of_messages : Conversation
                Number of messages which you want to recive

            Returns
            --------
            list of Message
                True if adding friend was sucessfull 
                False if failed

            Raises
            -------
            ValueError
                If there is no number_of_messages or timestamps in params
                or when time_to is later date than time_from
            """

            time_stamp_from    = kwargs['time_from']
            time_stamp_to      = kwargs['time_to']
            number_of_messages = kwargs['number_of_messages']

            messages = Message.objects.filter(conversation_fk=self.conversation)

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