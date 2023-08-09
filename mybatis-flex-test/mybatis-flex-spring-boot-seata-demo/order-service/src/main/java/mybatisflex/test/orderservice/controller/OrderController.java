/*
 *  Copyright 1999-2021 Seata.io Group.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package mybatisflex.test.orderservice.controller;

import io.seata.core.context.RootContext;
import mybatisflex.test.orderservice.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/order")
@RestController
public class OrderController {

    @Autowired
    OrderService orderService;

    @GetMapping(value = "/debit")
    public void debit(@RequestParam String userId, @RequestParam String commodityCode, @RequestParam Integer count) {
        System.out.println("order XID " + RootContext.getXID());
        orderService.create(userId, commodityCode, count);
    }

}
