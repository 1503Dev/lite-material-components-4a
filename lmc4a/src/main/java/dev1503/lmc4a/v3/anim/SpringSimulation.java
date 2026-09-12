package dev1503.lmc4a.v3.anim;

public class SpringSimulation {

    private float stiffness;
    private float dampingRatio;
    private float mass = 1f;
    private float position;
    private float velocity;
    private float target;
    private float dampingCoefficient;
    private static final float THRESHOLD = 0.001f;

    public SpringSimulation(float stiffness, float dampingRatio) {
        this.stiffness = stiffness;
        this.dampingRatio = dampingRatio;
        this.dampingCoefficient = 2f * dampingRatio * (float) Math.sqrt(stiffness);
    }

    public void setTarget(float target) {
        this.target = target;
    }

    public void setPosition(float position) {
        this.position = position;
        this.velocity = 0f;
    }

    public boolean isAtRest() {
        return Math.abs(position - target) < THRESHOLD
                && Math.abs(velocity) < THRESHOLD;
    }

    public float update(float deltaTime) {
        float displacement = position - target;
        float springForce = -stiffness * displacement;
        float dampingForce = -dampingCoefficient * velocity;
        float acceleration = (springForce + dampingForce) / mass;
        velocity += acceleration * deltaTime;
        position += velocity * deltaTime;
        return position;
    }
}
