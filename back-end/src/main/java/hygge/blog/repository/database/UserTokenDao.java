package hygge.blog.repository.database;

import hygge.blog.domain.local.enums.TokenScopeEnum;
import hygge.blog.domain.local.po.UserToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Xavier
 * @date 2022/7/20
 */
@Repository
public interface UserTokenDao extends JpaRepository<UserToken, Integer> {

    UserToken findUserTokenByUserIdAndAndScope(Integer userId, TokenScopeEnum scope);
}
