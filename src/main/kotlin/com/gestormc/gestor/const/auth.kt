package com.gestormc.gestor.const

import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
import com.mojang.authlib.yggdrasil.YggdrasilEnvironment
import java.net.Proxy

val AUTH_SERVICE = YggdrasilAuthenticationService(Proxy.NO_PROXY, null, YggdrasilEnvironment.PROD)
