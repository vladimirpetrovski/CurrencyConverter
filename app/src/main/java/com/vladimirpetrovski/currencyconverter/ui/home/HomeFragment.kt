package com.vladimirpetrovski.currencyconverter.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.vladimirpetrovski.currencyconverter.R
import com.vladimirpetrovski.currencyconverter.ui.utils.showSnackBar
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_home.*
import javax.inject.Inject

class HomeFragment : DaggerFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by viewModels<HomeViewModel> { viewModelFactory }

    @Inject
    lateinit var adapter: RatesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observe()
    }

    override fun onResume() {
        super.onResume()
        viewModel.load()
    }

    override fun onPause() {
        viewModel.unload()
        super.onPause()
    }

    private fun observe() {
        viewModel.list.observe(viewLifecycleOwner, Observer { list ->
            adapter.submitList(list)
        })
        viewModel.error.observe(viewLifecycleOwner, Observer {
            showSnackBar(
                String.format(getString(R.string.failed_to_load), it),
                Snackbar.LENGTH_INDEFINITE,
                getString(R.string.retry)
            ) {
                viewModel.retry()
            }
        })
        viewModel.selectCurrencyFinish.observe(viewLifecycleOwner, Observer {
            recyclerViewRates.scrollToPosition(0)
        })
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(context)
        recyclerViewRates.layoutManager = layoutManager
        recyclerViewRates.adapter = adapter
        recyclerViewRates.itemAnimator = RatesItemAnimator()
        recyclerViewRates.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    viewModel.pauseUpdates()
                } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    viewModel.resumeUpdates()
                }
            }
        })

        adapter.setOnRateClickListener {
            viewModel.pickCurrency(it)
        }

        adapter.setOnAmountChangeListener {
            viewModel.changeAmount(it)
        }
    }
}
