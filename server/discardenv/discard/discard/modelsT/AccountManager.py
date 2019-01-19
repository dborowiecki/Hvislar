from .Account import Account
from .Conversation import Conversation
from .ContactList import ContactList
from .Contact import Contact
class AccountManager():


	def __init__(self, account):
		self.account = account
	def add_friend(self, friend, c):		
		"""
	    Adding another account to account friend list.

	    Parameters
	    --------
	    friend : Account
	        Account of friend which is going to be added as friend

	    c : Conversation
	        Reference to conversation between accounts

	    Returns
	    --------
	    boolean
	        True if adding friend was sucessfull 
	        False if failed
		"""

		success = False
		try:
		    contact = Contact(account_fk =  friend, conversation_fk = c)
		    contact.save()
		    account_contact_list = ContactList(
		        account_fk = self.account,
		        contact_fk = contact,
		        friend_account_fk = friend)
		    account_contact_list.save()
		    success  = True
		except Exception as e:
		    print(e)
		finally:
		    return success

	def get_contact(self, account):
		"""
		Method contact with another user

		Parameters
		----------
		account: Account
		    Another account whose contact we want to recive

		Returns
		-------
		Contact
		    contact with another user if found
		    None if contact with another account was not found
		"""
		user_contcts = [f.contact_fk for f in ContactList.objects.filter(account_fk=self.account).all()]
		for contact in user_contcts:
		    if account == contact.account_fk:
		        return contact

		return None

	def get_contact_list(self):
	    user_contcts = [f.friend_account_fk.username for f in ContactList.objects.filter(account_fk=self.account).all()]
	    return user_contcts

	def add_description(self, description):
	    """
	    Method that adds description for account

	    Parameters
	    ----------
	    description: str
	        Description that we want add to account

	    Returns
	    -------
	    boolean
	        True if addition was succesfull
	        False if addition failed
	    """
	    try:
	        user_about = AccountAbout.objects.get(account_about_pk = self.account.account_pk)
	    except AccountAbout.DoesNotExist:
	        user_about = AccountAbout(account_about_pk = self.account)

	    user_about.description = description
	    user_about.save()
	    return True

	def get_description(self):
	    """
	    Getting account description

	    Returns
	    -------
	    str
	        account description 
	    """
	    try:
	        user_about = AccountAbout.objects.get(account_about_pk = self.account.account_pk)
	        return user_about.description
	    except AccountAbout.DoesNotExist:
	        raise ValueError('No description')
	    
	    return None
