import threading
import time
from django.db import models
# class xD():

# 	def __init__(self):
# 		self.kot = True

# 	def hange_state(self):
# 		if self.kot:
# 			self.kot = False
# 		else:
# 			self.kot = True

# 	def get_and_change(self):
# 		print("przed: "+str(self.kot))
# 		t = threading.Timer(5, self.hange_state)
# 		t.start()
# 		print("po: "+str(self.kot))
# 		return self.kot



# k = xD()
# k.get_and_change()
# time.sleep(5)
# print(k.kot)



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