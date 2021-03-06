# mysite/routing.py
"""
Used to redirect user to chat application in case of websoket connection
"""
from channels.auth import AuthMiddlewareStack
from channels.routing import ProtocolTypeRouter, URLRouter
from .QueryAuth import *
import chat.routing

application = ProtocolTypeRouter({
    # (http->django views is added by default)
    'websocket': QueryAuthMiddleware(
        URLRouter(
            chat.routing.websocket_urlpatterns
        )
    ),
})