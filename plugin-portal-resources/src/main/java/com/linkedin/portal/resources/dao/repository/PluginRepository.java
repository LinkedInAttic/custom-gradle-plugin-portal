/**
 * Copyright 2017 LinkedIn Corporation. All rights reserved. Licensed under the BSD-2 Clause license.
 * See LICENSE in the project root for license information.
 */
package com.linkedin.portal.resources.dao.repository;

import com.linkedin.portal.resources.dao.entity.PluginEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PluginRepository extends JpaRepository<PluginEntity, Long> {

    PluginEntity findByPluginNameEquals(String name);
}
