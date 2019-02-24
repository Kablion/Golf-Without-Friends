package de.kablion.golf.utils;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;

public class AnimationDrawable extends BaseDrawable {
        public final Animation anim;
        private float stateTime = 0;

        public AnimationDrawable(Animation anim)
        {
            this.anim = anim;
            setMinWidth(((TextureRegion)anim.getKeyFrame(0)).getRegionWidth());
            setMinHeight(((TextureRegion)anim.getKeyFrame(0)).getRegionHeight());
        }

        public void act(float delta)
        {
            stateTime += delta;
        }

        public void reset()
        {
            stateTime = 0;
        }

    @Override
    public void draw(Batch batch, float x, float y, float width, float height) {
        //super.draw(batch, x, y, width, height);
        batch.draw((TextureRegion)anim.getKeyFrame(stateTime), x, y, width, height);
    }

}
