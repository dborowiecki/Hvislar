import datetime
import threading
from asgiref.sync import async_to_sync
from collections import defaultdict
import discard.modelsT as model
class BattleRoyalManager():

	def __init__(self, channel_layer, channel_group):
		self.group = channel_group
		self.channel = channel_layer
		self.time = datetime.time(0, 0, 50)
		self.voting = True
		self.number_of_users = 1
		self.time_between_votings = 50 
		self.consumer_accounts = list()
		self.consumer_votes = {}

		#W TIMERZE Co 30 sekund oapalać głosowanie
	
	def start_battle_royale(self):
		#self.time_to_next_vote = self.count_time_to_next_vote()
		self.send_usernames()
		self.run_timer()

	def run_timer(self):
		t = threading.Timer(10, self.change_vote_state)
		t.start()

	def change_vote_state(self):
		print("Changing vote state")
		self.spread_the_news_about_vote()
		self.sum_up_voting()

		self.run_timer()

	def spread_the_news_about_vote(self):
		if self.voting is True:
			message = 'Lets count the votes'

		async_to_sync(self.channel.group_send)(
		    self.group, 
		    {
		        'type': 'voting_send',
		        'message': message,
		        'start' : self.voting
		    })

	def send_usernames(self):
		usernames = [account.username for account in self.consumer_accounts]

		async_to_sync(self.channel.group_send)(
		    self.group, 
		    {
		        'type': 'send_usernames',
		        'usernames': usernames,
		    })


	def add_consumer_account(self, consumer):
		self.consumer_accounts.append(consumer)

	def remove_consumer_account(self, consumer):
		self.consumer_accounts.remove(consumer)

	def count_time_to_next_vote(self):
		time = 60
		pass


	def addVote(self, account, voted_for):
		consumer_votes[account.username] = voted_for

	def sum_up_voting(self):
		counted_votes = defaultdict(int)
		for key, value in self.consumer_votes.items():
			counted_votes[value] = counted_votes[value]+1

		counted_votes['dobryKolega'] = 1
		for key, value in counted_votes.items():
			print(key+"   "+str(value))
		#TODO: ADD REMOVING MOST VOTED USERS
		pass









