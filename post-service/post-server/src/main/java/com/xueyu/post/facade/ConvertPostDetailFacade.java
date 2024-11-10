package com.xueyu.post.facade;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.xueyu.comment.client.CommentClient;
import com.xueyu.comment.request.PostCommentQueryRequest;
import com.xueyu.comment.sdk.vo.CommentPostVO;
import com.xueyu.common.core.result.ListVO;
import com.xueyu.common.core.result.RestResult;
import com.xueyu.common.web.facade.FacadeStrategy;
import com.xueyu.post.facade.request.ConvertDetailReq;
import com.xueyu.post.mapper.LikePostMapper;
import com.xueyu.post.mapper.PostGeneralMapper;
import com.xueyu.post.mapper.PostMapper;
import com.xueyu.post.mapper.TopicMapper;
import com.xueyu.post.pojo.domain.LikePost;
import com.xueyu.post.pojo.enums.PostIsAnonymousEnum;
import com.xueyu.post.pojo.vo.PostDetailVO;
import com.xueyu.post.pojo.vo.PostView;
import com.xueyu.post.pojo.vo.VoteVO;
import com.xueyu.post.service.VoteService;
import com.xueyu.user.client.UserClient;
import com.xueyu.user.sdk.pojo.utils.UserFactory;
import com.xueyu.user.sdk.pojo.vo.UserSimpleVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 处理帖子详情相关信息整合
 *
 * @author durance
 */
@Component
@Slf4j
public class ConvertPostDetailFacade implements FacadeStrategy<ConvertDetailReq, PostDetailVO> {

    @Resource
    LikePostMapper likePostMapper;

    @Resource
    UserClient userClient;

    @Resource
    VoteService voteService;

    @Resource
    TopicMapper topicMapper;

    @Resource
    CommentClient commentClient;

    @Resource
    PostGeneralMapper postGeneralMapper;

    @Resource
    UserFactory userFactory;

    @Override
    public PostDetailVO execBusiness(ConvertDetailReq convertDetailReq) {
        PostView postView = convertDetailReq.getPostView();
        Integer userId = convertDetailReq.getUserId();
        Integer postId = postView.getId();
        PostDetailVO postDetailVO = new PostDetailVO();
        // 拷贝相同属性项
        BeanUtils.copyProperties(postView, postDetailVO);
        // html标签转码
        postDetailVO.setContent(HtmlUtils.htmlUnescape(postView.getContent()));
        // 查询并设置是否点赞
        if (userId != null) {
            LambdaQueryWrapper<LikePost> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(LikePost::getPostId, postId).eq(LikePost::getUserId, userId);
            LikePost likePost = likePostMapper.selectOne(wrapper);
            postDetailVO.setIsLike(likePost != null);
        } else {
            postDetailVO.setIsLike(false);
        }
        // 设置点赞用户信息
        LambdaQueryWrapper<LikePost> likeWrapper = new LambdaQueryWrapper<>();
        likeWrapper.eq(LikePost::getPostId, postId);
        List<LikePost> likePosts = likePostMapper.selectList(likeWrapper);
        if (CollectionUtils.isNotEmpty(likePosts)){
            List<Integer> likeUserIds = likePosts.stream().map(LikePost::getUserId).collect(Collectors.toList());
            List<UserSimpleVO> userInfos = userClient.getUserDeatilInfoList(likeUserIds).getData();
            postDetailVO.setUserLikeList(userInfos);
        }
        // 查询并设置作者信息
        // 设置帖子用户信息
        if (PostIsAnonymousEnum.YES.getValue().equals(postView.getIsAnonymous())){
            postDetailVO.setUserInfo(userFactory.buildAnonymityUserInfo());
        }else{
            postDetailVO.setUserInfo(userClient.getUserInfo(postView.getUserId()).getData());
        }
        // 查询评论信息
        PostCommentQueryRequest request = new PostCommentQueryRequest();
        request.setPostId(postId);
        request.setUserId(userId);
        // 查询携带的话题
        postDetailVO.setTopics(topicMapper.selectByPostId(postId));
        // 设置投票信息
        VoteVO voteVO;
        if (userId != null) {
            voteVO = voteService.getVoteDetail(postId, userId);
        } else {
            voteVO = voteService.getVoteDetail(postId, null);
        }
        // 增加帖子阅读量 todo 设计方案优化，不每次都直接更新库中次数
        postGeneralMapper.incrPostViewNum(postId);
        postDetailVO.setVoteMessage(voteVO);
        return postDetailVO;
    }

}
