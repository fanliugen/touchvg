#import <UIKit/UIKit.h>

// 实现在点击视图以外的区域关闭该视图
@interface SCCalloutView : UIButton
{
}

@end

@class GiViewController;

@interface SCCalloutGraphView : SCCalloutView
{
    GiViewController    *_graphc;
}

@property (nonatomic,readonly)  GiViewController *graphc;

- (id)initWithFrame:(CGRect)frame graphc:(GiViewController*)g;

@end
