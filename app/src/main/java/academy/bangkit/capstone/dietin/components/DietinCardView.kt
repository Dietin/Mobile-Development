package academy.bangkit.capstone.dietin.components


//class DietinCardView: MaterialCardView {
//
//    private var cardZIndex: Float = 0f
//
//    constructor(context: Context): super(context)
//
//    constructor(context: Context, attrs: AttributeSet): super(context, attrs) {
//        initAttr()
//    }
//
//    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int): super(context, attrs, defStyleAttr) {
//        initAttr()
//    }
//
//    init {
//        radius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, resources.displayMetrics)
//        strokeWidth = 0
//        background = null
//        setCardBackgroundColor(AppCompatResources.getColorStateList(context, android.R.color.transparent))
//    }
//
//    private fun initAttr() {
//        val typedArray = context.obtainStyledAttributes(R.styleable.DietinCardView)
//        cardZIndex = typedArray.getDimension(R.styleable.DietinCardView_cardZIndex, 0f)
//        typedArray.recycle()
//    }
//
//    override fun setCardElevation(elevation: Float) {
//        super.setCardElevation(elevation)
//        cardZIndex = elevation
//    }
//
//    override fun getCardElevation(): Float {
//        return cardZIndex
//    }
//}