/*
**        DroidPlugin Project
**
** Copyright(c) 2015 Andy Zhang <zhangyong232@gmail.com>
**
** This file is part of DroidPlugin.
**
** DroidPlugin is free software: you can redistribute it and/or
** modify it under the terms of the GNU Lesser General Public
** License as published by the Free Software Foundation, either
** version 3 of the License, or (at your option) any later version.
**
** DroidPlugin is distributed in the hope that it will be useful,
** but WITHOUT ANY WARRANTY; without even the implied warranty of
** MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
** Lesser General Public License for more details.
**
** You should have received a copy of the GNU Lesser General Public
** License along with DroidPlugin.  If not, see <http://www.gnu.org/licenses/lgpl.txt>
**
**/

package com.morgoo.droidplugin.hook.proxy;

import android.content.Context;

import com.morgoo.droidplugin.hook.BaseHookHandle;
import com.morgoo.droidplugin.hook.handle.WebViewFactoryProviderHookHandle;
import com.morgoo.droidplugin.reflect.FieldUtils;
import com.morgoo.droidplugin.reflect.Utils;
import com.morgoo.helper.DroidProxy;
import com.morgoo.helper.compat.WebViewFactoryCompat;

import java.util.List;

/**
 * Created by Andy Zhang(zhangyong232@gmail.com) on 2014/10/10.
 */
public class WebViewFactoryProviderHook extends ProxyHook {

    public WebViewFactoryProviderHook(Context hostContext) {
        super(hostContext);
    }

    @Override
    protected BaseHookHandle createHookHandle() {
        return new WebViewFactoryProviderHookHandle(mHostContext);
    }

    @Override
    protected void onInstall(ClassLoader classLoader) throws Throwable {
        mOldObj = WebViewFactoryCompat.getProvider();
        Class<?> clazz = mOldObj.getClass();
        List<Class<?>> interfaces = Utils.getAllInterfaces(clazz);
        Class[] ifs = interfaces != null && interfaces.size() > 0 ? interfaces.toArray(new Class[interfaces.size()]) : new Class[0];
        Object newObj = DroidProxy.newProxyInstance(clazz.getClassLoader(), ifs, this);
        FieldUtils.writeStaticField(WebViewFactoryCompat.Class(), "sProviderInstance", newObj);
    }

}
