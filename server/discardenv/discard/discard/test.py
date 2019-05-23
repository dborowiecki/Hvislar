import threading
import time
from django.db import models


"""
This class is used to switch off adding new users to conversation that was created
"""
class conversation_closer():

	def __init__(self, conversation):
		self.kot = conversation

	def hange_state(self):
		self.kot.allow_new_users = False
		self.kot.save()

	def change(self):
		print("przed: "+str(self.kot))
		t = threading.Timer(50, self.hange_state)
		t.start()
