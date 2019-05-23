import datetime
import threading
import discard.modelsT as model
import discard.scripts.MassConversationManager as mag
import discard.chat_settings as s
from asgiref.sync import async_to_sync
from collections import defaultdict, OrderedDict
from operator import itemgetter  
class BattleRoyalManager():
	'''
	Class that manages battle royale logistic
	'''
	def __init__(self, channel_layer, channel_group, channel_name, room_name):
		self.group = channel_group
		self.channel = channel_layer
		self.room_name = room_name
		self.time = datetime.time(0, 0, 50)
		self.voting = True
		self.number_of_users = 1
		self.TIME_BETWEEN_VOTINGS = 50 
		self.consumer_accounts = list()
		self.discarded_accounts = list()
		self.consumer_channel_name = {}
		self.consumer_votes = {}
		self.channel_name = channel_name
		self.started = False
	
	def start_battle_royale(self):
		self.started = True
		self.send_usernames()
		self.run_timer()

	def run_timer(self):
		t = threading.Timer(self.TIME_BETWEEN_VOTINGS, self.change_vote_state)
		t.start()


	def change_vote_state(self):
		print("Changing vote state")

		to_kick = self.sum_up_voting()
		self._remove_users(to_kick)

		self.spread_the_news_about_vote(to_kick)

		if len(self.consumer_accounts) is 2:
			self._finish_battle()
		else:
			self.run_timer()


	def spread_the_news_about_vote(self, kicked):
		if self.voting is True:
			message = 'Lets count the votes,\nKicked: '+str(kicked)

		async_to_sync(self.channel.group_send)(
		    self.group, 
		    {
		        'type'   :'voting_send',
		        'message': message,
		        'removed': kicked 
		    })

	def send_usernames(self):
		usernames = [account.username for account in self.consumer_accounts]

		async_to_sync(self.channel.group_send)(
		    self.group, 
		    {
		        'type': 'send_usernames',
		        'usernames': usernames,
		    })

	def send_message_about_kick(self, usernames):
		for user in usernames:
			async_to_sync(self.channel.group_send)(
			    self.consumer_channel_name[user][0], 
			    {
			        'type': 'inform_about_kick',
			        'username': user,
			    })

	def disconnect_users(self,users):
		print("NO JESTEŚMY TUTAJ")
		print("USERZY: "+str(users))
		for user in users:
			if user not in self.discarded_accounts:
				async_to_sync(self.channel.group_discard)(
				    self.group,
				    self.consumer_channel_name[user][1]
				)
				self.discarded_accounts.append(user)



	def add_consumer_account(self, consumer, consumer_channel_name):
		self.consumer_accounts.append(consumer)
		self.consumer_channel_name[consumer.username] = consumer_channel_name
		print(self.consumer_accounts)

	def remove_consumer_accounts(self, consumer_names):
		self.send_message_about_kick(consumer_names)
		self.disconnect_users(consumer_names)
		self.change_state_in_database(consumer_names)
		self.consumer_accounts = [a for a in self.consumer_accounts 
		if a.username not in consumer_names]

		for account in consumer_names:
			try:
				self.consumer_channel_name.pop(account)
			except Exception as e:
				print('Unable to remove consumer channel' + str(e))


	def count_time_to_next_vote(self):
		time = 60
		pass


	def addVote(self, account, voted_for):
		self.consumer_votes[account.username] = voted_for

	def sum_up_voting(self):
		counted_votes = defaultdict(int)
		for key, value in self.consumer_votes.items():
			counted_votes[value] = counted_votes[value]+1

		for key, value in counted_votes.items():
			print(key+"   "+str(value))

		number_of_kicked = int(len(self.consumer_accounts)/3)+1
		x = OrderedDict(sorted(counted_votes.items(),key = itemgetter(1), reverse = True))
		kicked_usernames = list()

		for key in x.keys():
			kicked_usernames.append(key)

		kicked_usernames = kicked_usernames[:number_of_kicked]
		print('Kicked users:'+ str(number_of_kicked)+'\n'+str(kicked_usernames)+'\n-----------')		

		return kicked_usernames

	def _remove_users(self, usernames):
		self.remove_consumer_accounts(usernames)
		self.consumer_votes = {}

	def change_state_in_database(self, usernames):
		for user in usernames:
			mag.change_user_state(self.room_name, user)

	def _finish_battle(self):
		winner1 = self.consumer_accounts[0]
		winner2 = self.consumer_accounts[1]
		new_conversation = model.Conversation()
		new_conversation.save()
		added1 = model.AccountManager(winner1).add_friend(winner2, new_conversation)
		added2 = model.AccountManager(winner2).add_friend(winner1, new_conversation)


		async_to_sync(self.channel.group_send)(
		    self.group, 
		    {
		        'type': 'finish_send',
		        'message': "Good job, you won",
		    })

	def get_time_before_start(self, conv):

		if self.started:
		    return 0
		else:
		    a = conv.creation_date + datetime.timedelta(seconds = s.TIME_TO_CLOSE_CONVERSATION.second)
		    now = datetime.datetime.now()
		    diff =  a - now 

		    if diff.days < 0:
		        d = 0
		        self.started = True
		    else:
		        d = diff

		    return d










