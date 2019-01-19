import datetime
import threading
from asgiref.sync import async_to_sync
class BattleRoyalManager():

	def __init__(self, channel_layer, channel_group):
		self.group = channel_group
		self.channel = channel_layer
		self.accounts_in_chat = []
		self.time = datetime.time(0, 0, 50)
		self.voting = False
		self.number_of_users = 1,
		self.consumers = []


		#W TIMERZE Co 30 sekund oapalać głosowanie
	
	def start_battle_royale(self):
		self.time_to_next_vote = self.count_time_to_next_vote()
		run_timer()

	def run_timer(self):
		t = threading.Timer(50, self.voting)
		t.start()

	def change_vote_state(self):
		print("Voting start")
		if self.voting is False:
			self.voting = True
			self.spread_the_news_about_vote()
		else:
			self.voting = False
			self.spread_the_news_about_vote()
			#self.sum_up_voting()

	def spread_the_news_about_vote(self):
		if self.voting is True:
			message = 'Time to vote'
		else:
			message = 'Vote ending'

		async_to_sync(self.channel.group_send)(
		    self.group, 
		    {
		        'type': 'voting_send',
		        'message': message,
		        'start' : self.voting
		    })


	def add_consumer(self, consumer):
		self.consumers.add(consumer)

	def remove_consumer(self, consumer):
		self.consumers.remove(consumer)








