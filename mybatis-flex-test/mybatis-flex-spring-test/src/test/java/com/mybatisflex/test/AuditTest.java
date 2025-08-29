package com.mybatisflex.test;

import com.mybatisflex.core.audit.AuditManager;
import com.mybatisflex.core.audit.AuditMessage;
import com.mybatisflex.core.audit.MessageCollector;
import com.mybatisflex.test.mapper.AccountMapper;
import com.mybatisflex.test.mapper.TbClassMapper;
import lombok.Getter;
import org.assertj.core.api.WithAssertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
public class AuditTest implements WithAssertions {
    @Autowired
    AccountMapper accountMapper;
    @Autowired
    TbClassMapper  tbClassMapper;

    @Test
    public void test() {
        TestMsgCollector collector = new TestMsgCollector();
        AuditManager.setMessageCollector(collector);
        AuditManager.setAuditEnable(true);
        accountMapper.selectAccounts();
        tbClassMapper.selectAll();
        List<AuditMessage> messages = collector.getMessages();
        assertThat(messages.size()).isEqualTo(2);
        assertThat(messages.get(0).getStmtId()).isEqualTo("com.mybatisflex.test.mapper.AccountMapper.selectAccounts");
        assertThat(messages.get(1).getStmtId()).isEqualTo("com.mybatisflex.test.mapper.TbClassMapper.selectListByQuery");
    }

    static class TestMsgCollector implements MessageCollector {
        @Getter
        private final List<AuditMessage> messages = new ArrayList<>();

        @Override
        public void collect(AuditMessage message) {
            messages.add(message);
        }
    }
}
