import datetime
import threading
import discard.modelsT as model
import discard.scripts.MassConversationManager as mag
from asgiref.sync import async_to_sync
from collections import defaultdict, OrderedDict
from operator import itemgetter  
class BattleRoyalManager():

	def __init__(self, channel_layer, channel_group, channel_name, room_name):
		self.group = channel_group
		self.channel = channel_layer
		self.room_name = room_name
		self.time = datetime.time(0, 0, 50)
		self.voting = True
		self.number_of_users = 1
		self.time_between_votings = 50 
		self.consumer_accounts = list()
		self.consumer_channel_name = {}
		self.consumer_votes = {}
		self.channel_name = channel_name

		#W TIMERZE Co 30 sekund oapalać głosowanie
	
	def start_battle_royale(self):
		#self.time_to_next_vote = self.count_time_to_next_vote()
		self.send_usernames()
		#remove later
		self.run_timer()

	def run_timer(self):
		t = threading.Timer(10, self.change_vote_state)
		t.start()


	def change_vote_state(self):
		print("Changing vote state")
		to_kick = self.sum_up_voting()
		self.remove_users(to_kick)

		self.spread_the_news_about_vote(to_kick)

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
			#print(type(self.consumer_channel_name[user][0]))
			async_to_sync(self.channel.group_send)(
			    self.consumer_channel_name[user][0], 
			    {
			        'type': 'inform_about_kick',
			        'username': user,
			    })
		#	except Exception as e:
		#		print("cannot inform user "+user+" "+str(e))

	def disconnect_users(self,users):
		for user in users:
			async_to_sync(self.channel.group_discard)(
	            self.group,
	            self.consumer_channel_name[user][1]
	        )



	def add_consumer_account(self, consumer, consumer_channel_name):
		self.consumer_accounts.append(consumer)
		self.consumer_channel_name[consumer.username] = consumer_channel_name

	def remove_consumer_accounts(self, consumer_names):
		self.send_message_about_kick(consumer_names)
		self.change_state_in_database(consumer_names)
		self.disconnect_users(consumer_names)
		self.consumer_accounts = [a for a in self.consumer_accounts 
		if a.username not in consumer_names]

		for account in consumer_names:
		#try:
			self.consumer_channel_name.pop(account)
		#	except Exception as e:
		#		print('Unable to delete consumer channel' + str(e))


	def count_time_to_next_vote(self):
		time = 60
		pass


	def addVote(self, account, voted_for):
		consumer_votes[account.username] = voted_for

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

	def remove_users(self, usernames):
		self.remove_consumer_accounts(usernames)
		self.consumer_votes = {}

	def change_state_in_database(self, usernames):
		for user in usernames:
			mag.change_user_state(self.room_name, user)












