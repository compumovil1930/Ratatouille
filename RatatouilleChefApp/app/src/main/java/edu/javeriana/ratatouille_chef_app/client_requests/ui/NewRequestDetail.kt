package edu.javeriana.ratatouille_chef_app.client_requests.ui


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import edu.javeriana.ratatouille_chef_app.R

/**
 * A simple [Fragment] subclass.
 */
class NewRequestDetail : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_request_detail, container, false)
    }



}
