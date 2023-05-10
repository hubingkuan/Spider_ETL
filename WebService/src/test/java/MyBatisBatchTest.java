

import com.yeshen.appcenter.WebAppcenterApplication;
import com.yeshen.appcenter.domain.entity.PostMeta;
import com.yeshen.appcenter.repository.mysql.BannerDAO;
import com.yeshen.appcenter.repository.mysql.GameDAO;
import com.yeshen.appcenter.repository.mysql.PostMetaDAO;
import com.yeshen.appcenter.utils.MybatisBatchUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;

/**
 * Date 2022/05/05  14:28
 * author by HuBingKuan
 */
@SpringBootTest(classes = WebAppcenterApplication.class)
@RunWith(SpringRunner.class)
public class MyBatisBatchTest {
    @Autowired
    MybatisBatchUtil mybatisBatchUtils;

    @Resource
    PostMetaDAO postMetaDAO;

    @Resource
    GameDAO gameDAO;

    @Resource
    BannerDAO bannerDAO;


    @Test
    public void batchTest(){
/*        DataSourceContextHolder.setDatabaseHolder(SupportDatasourceEnum.DEV_CN);
        List<GameDisplayRepVO> list = gameDAO.getAllEffectivePackageNames();
        ArrayList<Game> games = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            GameDisplayRepVO tem = list.get(i);
            Game game = new Game();
            game.setGameName(tem.getGameName());
            game.setPackageName(tem.getPackageName());
            game.setUpdateTime(tem.getLastModTime());
            games.add(game);
        }
        // 10万条数据耗时895639ms=15分钟
        System.out.println("成功条数:"+mybatisBatchUtils.batchUpdateOrInsert(games, GameDAO.class, (item, GameDAO) -> gameDAO.insert(item)));
        DataSourceContextHolder.clear();*/
    }

    @Test
    public void commonInsert(){
        ArrayList<PostMeta> postMetas = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            PostMeta postMeta = new PostMeta();
            postMeta.setPostId(i+999999L);
            postMeta.setMetaId(i+999999L);
            postMeta.setMetaValue(i+"999999");
            postMeta.setMetaKey(i+"999999");
            postMetas.add(postMeta);
        }
        long startTime=System.currentTimeMillis();
        postMetas.forEach(e->postMetaDAO.insert(e));
        // 10万条数据耗时1448130ms=24分钟
        System.out.println("耗时:"+(System.currentTimeMillis()-startTime));
    }

    @Test
    public void name() {
        int i = bannerDAO.updateById(bannerDAO.selectById(1));
        System.out.println("返回值:"+i);
    }
}