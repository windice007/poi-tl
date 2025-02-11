/*
 * Copyright 2014-2021 Sayi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.deepoove.poi.policy.reference;

import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.deepoove.poi.exception.RenderException;
import com.deepoove.poi.policy.RenderPolicy;
import com.deepoove.poi.template.ElementTemplate;
import com.google.gson.internal.LinkedTreeMap;

import net.jodah.typetools.TypeResolver;

public abstract class AbstractTemplateRenderPolicy<E extends ElementTemplate, T> implements RenderPolicy {

    @SuppressWarnings("unchecked")
    protected T castFromJson(Configure configure, LinkedTreeMap<?, ?> source) {
        Class<?>[] typeArguments = TypeResolver.resolveRawArguments(AbstractTemplateRenderPolicy.class, getClass());
        return (T) configure.getGsonHandler().castJsonToClass(source, typeArguments[1]);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void render(ElementTemplate eleTemplate, Object data, XWPFTemplate template) {
        if (null == data) return;
        // type safe
        T model = null;
        try {
            Object source = data;
            if (data instanceof LinkedTreeMap) {
                source = castFromJson(template.getConfig(), (LinkedTreeMap<?, ?>) data);
            }
            model = (T) source;
        } catch (ClassCastException e) {
            throw new RenderException("Error Render Data format for template: " + eleTemplate, e);
        }

        try {
            doRender((E) eleTemplate, model, template);
        } catch (Exception e) {
            if (e instanceof RenderException)
                throw (RenderException) e;
            else
                throw new RenderException("TemplateRenderPolicy render error", e);
        }

    }

    public abstract void doRender(E eleTemplate, T data, XWPFTemplate template) throws Exception;

}
