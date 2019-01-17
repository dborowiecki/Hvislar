# from django.db import models
# from django.template.defaultfilters import slugify
# from .MassConversation import MassConversation
# from .Account import Account

# class AccountInMassConversation(models.Model):
#     """
#     A model which stores information about account in group conversation.


#     Attributes
#     -------
#     conversation_fk : MassConversation
#         Reference to conversation of which user is participant

#     user_fk : Account
#         Reference to user account which is in conversation

#     user_removed : boolean
#         Checks if user is still in conversation

#     """
#     conversation_fk   = models.ForeignKey(MassConversation, on_delete=models.CASCADE, primary_key=True)
#     user_fk           = models.ForeignKey(Account, on_delete=models.CASCADE)
#     user_removed      = models.BooleanField(default=False)

#     class Meta:
#         db_table = '"accounts_in_mass_conversation"'
#         unique_together = ('conversation_fk', 'user_fk')
